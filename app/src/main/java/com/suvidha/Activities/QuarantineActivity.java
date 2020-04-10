package com.suvidha.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.suvidha.Adapters.QuarantineAdapter;
import com.suvidha.Fragment.Emegency_Dialog;
import com.suvidha.Fragments.HistoryFragment;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.GetReportsModel;
import com.suvidha.Models.ReportModel;
import com.suvidha.R;
import com.suvidha.Receiver.AlarmReceiver;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.IntToDoubleFunction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.suvidha.Utilities.Utils.CAMERA_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;


public class QuarantineActivity extends AppCompatActivity implements Emegency_Dialog.DialogListener {

    private static final int CAMERA_REQUEST = 5;
    private static final int GPS_REQUEST_CODE = 10;
    private FusedLocationProviderClient mFusedLocationClient;
    private Toolbar toolbar;
    private AppBarLayout toolbar_layout;
    ApiInterface apiInterface;
    private int location_error = 0;
    private static final int THRESHOLD_DIST = 200;
    private double lat, lon;
    private Button reportBtn;
    private RecyclerView rview;
    private QuarantineAdapter mAdapter;
    private RelativeLayout pFrame;
    private List<ReportModel> data = new ArrayList<>();
    static int MINUTES=120;
    static int LAST_UPDATE=0;
    private TextView dayleftcount,daysleftmessege;
    public static final String MY_PREFS_NAME = "Last_Update";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarantine);
        init();

        setRecyclerView();
        intialiseRetrofit();
        getReports();
//        lat = getIntent().getDoubleExtra("lat", 0);
//        lon = getIntent().getDoubleExtra("lon", 0);
        setToolbar();
        /*if(currentLocation == null){
            pFrame.setVisibility(View.VISIBLE);
            reportBtn.setEnabled(false);
            reportBtn.setText(getResources().getString(R.string.please_wait));
        }*/
//        calDiffDist();
          check();



    }
    private void getLocation()
    {
        Intent intent=getIntent();
      lat=  intent.getFloatExtra("lat",0.0f);
      lon= intent.getFloatExtra("lon",0.0f);

        reportBtn.setEnabled(true);
        reportBtn.setText(getResources().getString(R.string.report));
        calDiffDist();

        pFrame.setVisibility(View.GONE);
    }


    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (checkLocationPermission()) {
            //first get current location as quarantine location
            //then open dialog
            if (canGetLocation()) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,0, 0, locationListener);
            } else {
                showSettingsAlert();
            }

        } else {
            requestLocationPermissions();
//                    Toast.makeText(getContexgetLocationUpdatest(), "You don't have location permission", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE
        );
    }
    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (lm == null)

            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        try {
            network_enabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled == false || network_enabled == false) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }
    public void showSettingsAlert() {
        Dialog dialog = createAlertDialog(this,getResources().getString(R.string.error),"Please turn on GPS","Go Back","Ok");
        dialog.setCancelable(false);
        // Setting Dialog Title
       dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(
                       Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               startActivityForResult(intent,GPS_REQUEST_CODE);
               dialog.dismiss();
           }
       });
       dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
    }

    //    private Dialog dialog;
//    private ProgressBar progressBar;
    private void getReports() {
//        if (dialog == null) {
//            dialog = createProgressDialog(this, getResources().getString(R.string.please_wait));
//        }
//        progressBar = dialog.findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
//        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
//        staticProgress.setVisibility(View.GONE);
//        dialog.show();
        pFrame.setVisibility(View.VISIBLE);
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
                mAdapter.notifyDataSetChanged();
                pFrame.setVisibility(View.GONE);
                setLeftDays(response.body().left);
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
                pFrame.setVisibility(View.GONE);
                Toast.makeText(QuarantineActivity.this, getResources().getString(R.string.cannot_get_your_reports), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLeftDays(int left) {
        if(left<=0){
            daysleftmessege.setText(getResources().getString( R.string.police_verification));
            left=0;
        }
        dayleftcount.setText(String.valueOf(left));
    }


    private void setRecyclerView() {
        rview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new QuarantineAdapter(this,data);
        rview.setAdapter(mAdapter);

    }

    private void calDiffDist() {
        float qlat= SharedPrefManager.getInstance(this).getFloat(SharedPrefManager.Key.QUARENTINE_LAT_KEY,0);
        float qlon= SharedPrefManager.getInstance(this).getFloat(SharedPrefManager.Key.QUARENTINE_LON_KEY,0);
        double d = distance((double) qlat,(double) qlon,lat,lon,"K")*1000;
//        Log.e("TAG", String.valueOf(d));
//        Log.e("QUARANTINE",qlat+", "+qlon);
//        Log.e("CURRENT",lat+", "+lon);
//        Toast.makeText(this, "DIST:"+d+" LAT:"+currentLocation.getLatitude()+" LON:"+currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        if(d>THRESHOLD_DIST){
            location_error=1;
            toolbar_layout.setBackgroundColor(Color.RED);
        }else{
            location_error = 0;
            toolbar_layout.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void init() {
        dayleftcount=findViewById(R.id.leftdays);
        daysleftmessege=findViewById(R.id.leftdaysmessage);
        rview = findViewById(R.id.quarantine_rview);
        pFrame = findViewById(R.id.progress_frame);
        pFrame.setVisibility(View.GONE);
        toolbar = findViewById(R.id.default_toolbar);
        toolbar_layout = findViewById(R.id.main_app_bar);
        reportBtn = findViewById(R.id.report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkCameraPermission()){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                        startActivityForResult(intent,CAMERA_REQUEST);
                    }else{
                        requestCameraPermission();
                    }
                }
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
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.Quarantine));
    }
    void sendDataToServer(Intent data){
        try {
            pFrame.setVisibility(View.VISIBLE);
//            Dialog dialog = createProgressDialog(getApplicationContext(),getResources().getString(R.string.please_wait));
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            ReportModel model = new ReportModel(encoded,(float) lat,(float)lon,"hvjhvjh",location_error);
            Call<GeneralModel> call = apiInterface.send_report(getAccessToken(getApplicationContext()),model);
            call.enqueue(new Callback<GeneralModel>() {
                @Override
                public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
//                    dialog.dismiss();
                    pFrame.setVisibility(View.GONE);
                    getReports();
                    Dialog alertDialog = createAlertDialog(QuarantineActivity.this,getResources().getString(R.string.successful),getResources().getString(R.string.submitter_successfully),"",getResources().getString(R.string.ok));
                    setRemainder();
                    alertDialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
//                    Toast.makeText(QuarantineActivity.this, "Report Submited", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<GeneralModel> call, Throwable t) {
                    Log.e("TAG",t.getMessage());
                    pFrame.setVisibility(View.GONE);
//                    dialog.dismiss();
                    Toast.makeText(QuarantineActivity.this, getResources().getString(R.string.failed_to_submit_report), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + R.string.emergency_phone_no));
        startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_quarantine,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return false;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );
    }


    public boolean checkCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
          if(loc!=null) {

          }

        }

        @Override
        public void onProviderDisabled(String provider) {
            try {
                if (checkLocationPermission()) {
                    //first get current location as quarantine location
                    //then open dialog
                    if (canGetLocation()) {

                    } else {
                        showSettingsAlert();
                    }

                } else {
                    requestLocationPermissions();
//                    Toast.makeText(getContext(), "You don't have location permission", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

            }

        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                startActivityForResult(intent,CAMERA_REQUEST);

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }else if (requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                if (canGetLocation()) {
                    getLocationUpdates();
                    Toast.makeText(this, getResources().getString(R.string.loc_perm_denied), Toast.LENGTH_SHORT).show();
                } else {
                    showSettingsAlert();
                }

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == RESULT_OK){
                sendDataToServer(data);
            }else{
                Toast.makeText(this, getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == GPS_REQUEST_CODE){
            if(resultCode == 1){
                getLocationUpdates();
            }else{
                finish();
            }
        }
    }
    public boolean isTimeUp(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        LAST_UPDATE = prefs.getInt("time", LAST_UPDATE); //0 is the default value.
        return (System.currentTimeMillis()-LAST_UPDATE)/(60*1000)>=MINUTES;
    }
    private void check() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        long check= prefs.getLong("time", -1); //0 is the default value.
        if(check == -1) {
            setRemainder();
        }
    }

    public void cancelRemainder(){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong("time", System.currentTimeMillis());
        editor.apply();
        Log.d("ak47", "cancelRemainder: ");
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.setAction("com.suvidha.Activities");
        intent.putExtra("SET","STOP");
        sendBroadcast(intent);
    }
    public void setRemainder(){

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("SET","RUN");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+MINUTES*60*1000,
                pendingIntent);
        Log.d("ak47", "setRemainder: ");
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
    //    private class LocationStuff extends AsyncTask<Void, Void, Void> {
//
//        private Location currentLocation;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            getCurrentLocation();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            //get current location
//            while (currentLocation == null) {
////                Log.e("LOL", "lol");
//            }
//
//            //get nodes location
//            //punch cur and nod loc in list
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.e("LOC", String.valueOf(currentLocation));
//
//
//        }
//
////        private void getCurrentLocation() {
////            mFusedLocationClient.getLastLocation().addOnCompleteListener(
////                    new OnCompleteListener<Location>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Location> task) {
////                            Location location = task.getResult();
////                            if (location == null) {
////                                //select the node
//////                            Log.e(TAG, "Response Error " + .getMessage());
////
////                                TextView msg = dialog.findViewById(R.id.progress_msg);
////                                msg.setText("Try Again");
////                                new Handler().postDelayed(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        progressBar = dialog.findViewById(R.id.progress_bar);
////                                        progressBar.setVisibility(View.INVISIBLE);
////                                        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
////                                        staticProgress.setVisibility(View.VISIBLE);
////                                        staticProgress.setOnClickListener(new View.OnClickListener() {
////                                            @Override
////                                            public void onClick(View v) {
////                                                getCurrentLocation();
////                                            }
////                                        });
////                                    }
////                                }, 500);
////                                Toast.makeText(getContext(), "Can't get your location", Toast.LENGTH_SHORT).show();
////                            } else {
////                                quarantineLocation = location;
////                                dialog.dismiss();
////                                currentLocation = quarantineLocation;
////                                if(is_quarantined==1){
////                                    Intent intent = new Intent(getContext(), QuarantineActivity.class);
////                                    startActivity(intent);
////                                }else {
////                                    createQuarentineDialog();
////                                }
////                            }
////                        }
////                    }
////            );
////        }

//
//        Location initLoc(Double lat, Double lng) {
//            Location location = new Location("Provider");
//            location.setLatitude(lat);
//            location.setLongitude(lng);
//            return location;
//        }
//    }

}
