package com.suvidha.Utilities;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.GetReportsModel;
import com.suvidha.Models.LocationModel;
import com.suvidha.Models.ReportModel;
import com.suvidha.R;
import com.suvidha.Receiver.AlarmReceiver;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.local_zone_name;

public class LiveLocationService extends Service {
    ApiInterface apiInterface;
    private List<ReportModel> data = new ArrayList<>();
    Intent mServiceIntent;
    Location lastKnown;
    int is_quar;
    float qlat;
    float qlon;
    private static final int THRESHOLD_DIST = 300;

    LocationManager locationManager;
    private void sendLat(Location location) {
        LocationModel model=new LocationModel((float)location.getLatitude(),(float)location.getLongitude());
        Log.d("tester", String.valueOf(model.location_lat));
        Call<GeneralModel> registerResult = apiInterface.report_violation(getAccessToken(LiveLocationService.this), model);
        registerResult.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                if (response.body().status == 200) {
                    Log.d("response12","success");
                    Toast.makeText(LiveLocationService.this, "Report sent to Police", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(LiveLocationService.this, "Report failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
              //  Toast.makeText(LiveLocationService.this, getResources().getString(R.string.failed_to_register_quarantine), Toast.LENGTH_SHORT).show();
                Log.e("checkererr",t.getMessage());
            }
        });


    }

    public class TimestampSorter implements Comparator<ReportModel>
    {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        @Override
        public int compare(ReportModel o1, ReportModel o2) {
            try {
                if(f.parse(o2.report_time).before(f.parse(o1.report_time))){
                    return -10;
                }else{
                    return 10;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // This won't be a bound service, so simply return null
        return null;
    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void getReports() {
//        if (dialog == null) {
//            dialog = createProgressDialog(this, getResources().getString(R.string.please_wait));
//        }
//        progressBar = dialog.findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
//        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
//        staticProgress.setVisibility(View.GONE);
//        dialog.show();
        Call<GetReportsModel> getReportsModelCall = apiInterface.get_report(getAccessToken(this));
        getReportsModelCall.enqueue(new Callback<GetReportsModel>() {
            @Override
            public void onResponse(Call<GetReportsModel> call, Response<GetReportsModel> response) {
//                dialog.dismiss();
                data.clear();
                data.addAll(response.body().id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    data.sort(new TimestampSorter());
                }

            }

            @Override
            public void onFailure(Call<GetReportsModel> call, Throwable t) {
//                TextView msg = dialog.findViewById(R.id.progress_msg);
//                msg.setText(R.string.try_again);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar = dialog.findViewById(R.id.progress_bar);
//                        progressBar.setVisibility(View.INVISIBLE);
//                        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
//                        staticProgress.setVisibility(View.VISIBLE);
//                        staticProgress.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                getReports();
//                            }
//                        });
//                    }
//                }, 500);
            }
        });
    }
    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }




    @Override
    public void onTaskRemoved(Intent rootIntent) {
        int is_quarantine = SharedPrefManager.getInstance(this).getInt(SharedPrefManager.Key.IS_QUARANTINE);
        if(is_quarantine == 1) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // stoptimertask();
        int is_quarantine = SharedPrefManager.getInstance(this).getInt(SharedPrefManager.Key.IS_QUARANTINE);
        if(is_quarantine == 1) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }

    }
    public void setRemainder(){
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.setAction("start");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
      String  lastrep = SharedPrefManager.getInstance(LiveLocationService.this).getString(SharedPrefManager.Key.LAST_REPORTED);
        Log.d("lastrep",lastrep+" ");
      if(lastrep==null)
      {

          alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+10000,pendingIntent);
      }
      else {
         Handler handler = new Handler();

          final Runnable r = new Runnable() {
              public void run() {
                  try {
                      String string1 = "08:00:00";
                      Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
                      Calendar calendar1 = Calendar.getInstance();
                      calendar1.setTime(time1);
                      calendar1.add(Calendar.DATE, 1);


                      String string2 = "21:00:00";
                      Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
                      Calendar calendar2 = Calendar.getInstance();
                      calendar2.setTime(time2);
                      calendar2.add(Calendar.DATE, 1);
                      String currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                      Log.d("current date",currentDateAndTime);

                      String someRandomTime =currentDateAndTime.substring(currentDateAndTime.indexOf(' ')+1);
                      Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
                      Calendar calendar3 = Calendar.getInstance();
                      calendar3.setTime(d);
                      calendar3.add(Calendar.DATE, 1);

                      Date x = calendar3.getTime();
                      Log.d("limits",x.after(calendar1.getTime())+" "+someRandomTime+" "+string1);
                      Log.d("limits",x.after(calendar2.getTime())+" "+someRandomTime+" "+string2);
                      if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                          //checkes whether the current time is between 14:49:00 and 20:11:13.
                          Log.d("cheker","true");
                          String current = new SimpleDateFormat("HH:mm").format(new Date());
                          String  lastreps = SharedPrefManager.getInstance(LiveLocationService.this).getString(SharedPrefManager.Key.LAST_REPORTED);
                          if((Integer.parseInt( current.substring(0,2))- Integer.parseInt( lastreps.substring(0,2))>2)||((Integer.parseInt( current.substring(0,2))- Integer.parseInt( lastreps.substring(0,2))==2)&&Integer.parseInt( current.substring(3))- Integer.parseInt( lastreps.substring(3))>=10))
                          {
                              String  lastrepss = SharedPrefManager.getInstance(LiveLocationService.this).getString(SharedPrefManager.Key.LAST_REPORTED);

                              Log.d("inthreadd","set"+lastreps);

                              Calendar cal = Calendar.getInstance();

                              cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(current.substring(0, 2)));
                              cal.set(Calendar.MINUTE, Integer.parseInt(current.substring(3)));
                              cal.set(Calendar.SECOND, 10);
                              cal.set(Calendar.MILLISECOND, 0);
                              alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                             /* Log.d("inthreadd","set");
                              Calendar cal = Calendar.getInstance();

                              cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(current.substring(0, 2)));
                              cal.set(Calendar.MINUTE, Integer.parseInt(current.substring(3)) + 1);
                              cal.set(Calendar.SECOND, 0);
                              cal.set(Calendar.MILLISECOND, 0);
                              alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);*/
                          }
                          else
                          {
                              Log.d("inthreadd","not");

                          }


                      }
                      else
                      {


                          Log.d("cheker","false");
                      }
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }




                  handler.postDelayed(this, 1000*60*30);
              }
          };

          handler.postDelayed(r, 1000*60*30);


      }
       /* alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                120*60*1000,
                pendingIntent);*/
       // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificationManager.notify(2,getNotificationAlarm());

/*
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("SET","RUN");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+MINUTES*60*1000,
                pendingIntent);
        Log.d("ak47", "setRemainder: ");*/
//        // Set notificationId & text.
//        Intent intent = new Intent(QuarantineActivity.this, AlarmReceiver.class);
//        intent.putExtra("notificationId", 1);
//
//        // getBroadcast(context, requestCode, intent, flags)
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0,
//                intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
//        long alarmStartTime=System.currentTimeMillis()+1000*10;
//        Toast.makeText(this,alarmStartTime+" ",Toast.LENGTH_LONG).show();
//        Log.d("ak47", alarmStartTime+"setRemainder: "+System.currentTimeMillis());
//        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
    }

    @Override
    public void onCreate() {
        // This will be called when your Service is created for the first time
        // Just do any operations you need in this method.
        try {
            String string1 = "08:00:00";
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.DATE, 1);


            String string2 = "21:00:00";
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);
            String currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            Log.d("current date",currentDateAndTime);

            String someRandomTime =currentDateAndTime.substring(currentDateAndTime.indexOf(' ')+1);
            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            Log.d("limits",x.after(calendar1.getTime())+" "+someRandomTime+" "+string1);
            Log.d("limits",x.after(calendar2.getTime())+" "+someRandomTime+" "+string2);
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                Log.d("cheker","true");
                setRemainder();


            }
            else
            {


                Log.d("cheker","false");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("serviceStared", "gun");
       // setRemainder();
        qlat=SharedPrefManager.getInstance(LiveLocationService.this).getFloat(SharedPrefManager.Key.QUARENTINE_LAT_KEY,0.0f);
        qlon=SharedPrefManager.getInstance(LiveLocationService.this).getFloat(SharedPrefManager.Key.QUARENTINE_LON_KEY,0.0f);
        Log.d("sharedloc", String.valueOf(qlat));
        //to test if the servive is running
        intialiseRetrofit();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                30, locationListenerGPS);




            Location lastlocation=getLastKnownLocation();
            Log.d("testloc", String.valueOf(lastlocation));
            //Toast.makeText(LiveLocationService.this, (CharSequence) lastlocation,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Intent notificationIntent = new Intent(this, MainActivity.class);

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);*/

        /*Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.progress_icon)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();*/
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Location being accessed");
        bigText.setBigContentTitle("Live Location Being Accessed");
        bigText.setSummaryText("");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("");
        mBuilder.setContentText("");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }



        startForeground(1337,getNotification());

    }
    public Notification getNotificationAlarm()
    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "Demo Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent snoozeIntent = new Intent(this, AlarmReceiver.class);
        snoozeIntent.setAction("silent");
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);

        Intent intent = new Intent(LiveLocationService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(LiveLocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(LiveLocationService.this)
                .setContentTitle("Quarantine Alarm")
                .addAction(R.drawable.ic_clock, "Cancel",
                        snoozePendingIntent)
                .setContentText("Your need to send your selfie to Police administration!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        return notificationBuilder.build();
    }
    public Notification getNotification()
    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "Demo Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(LiveLocationService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(LiveLocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(LiveLocationService.this)
                .setContentTitle("Quarantine Patient")
                .setContentText("Your Live Location Is Being Monitored")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        return notificationBuilder.build();
    }
    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true)
                .setContentTitle("COVID19")
                .setContentText("Live Location being taken")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                ;

        return(b.build());
    }
    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            Log.d("Locchanged","changed");
          //  Toast.makeText(LiveLocationService.this, msg, Toast.LENGTH_LONG).show();

            double d = distance((double) qlat,(double) qlon,location.getLatitude(),location.getLongitude(),"K")*1000;
//        Log.e("TAG", String.valueOf(d));
//        Log.e("QUARANTINE",qlat+", "+qlon);
//        Log.e("CURRENT",lat+", "+lon);
//        Toast.makeText(this, "DIST:"+d+" LAT:"+currentLocation.getLatitude()+" LON:"+currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
            if(d>THRESHOLD_DIST){
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0,getNotificationWarn());
                sendLat(location);



                // toolbar_layout.setBackgroundColor(Color.RED);
            }else{
           /* location_error = 0;
            toolbar_layout.setBackgroundColor(getResources().getColor(R.color.green));*/
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    public Notification getNotificationWarn()
    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "Demo Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(LiveLocationService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(LiveLocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(LiveLocationService.this)
                .setContentTitle("Warning !!!!")
                .setContentText("You are out of quarantine premises!!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        return notificationBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }
    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.


                }
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    private boolean checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }



}