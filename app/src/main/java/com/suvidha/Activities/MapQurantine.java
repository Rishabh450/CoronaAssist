package com.suvidha.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.suvidha.Models.FetchNgomodel;
import com.suvidha.Models.GetReportsModel;
import com.suvidha.Models.NgoModel;
import com.suvidha.Models.SQDetail;
import com.suvidha.Models.SQList;
import com.suvidha.R;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.*;
import static android.content.pm.PackageManager.*;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;

public class MapQurantine extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener
{
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    GoogleMap mMap;
    Switch patrol;
    Intent mServiceIntent;
    Location lastKnown;
    ImageView emergency;
    ApiInterface apiInterface;
    private static final int CALL_PHONE_CODE = 7;
    List<SQDetail> data=new ArrayList<>();
    String vehicle = "rishabhKaGaadi";
    int flag = 0;
    private static final int GPS_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_qurantine);
        intialiseRetrofit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);

        mapFragment.getMapAsync(this);
        if (checkLocationPermission()) {
            //first get current location as quarantine location
            //then open dialog
            if (canGetLocation()) {
                Log.e("TAG","LOLOLO");
                getCurrentLocation();
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
        Dialog dialog = createAlertDialog(this,getResources().getString(R.string.error),getResources().getString(R.string.turn_on_gps),getResources().getString(R.string.back),getResources().getString(R.string.ok));
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
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("LOL", "WTH");
        if (requestCode == LOCATION_PERMISSION_CODE) {
            Toast.makeText(this, "WTH " + requestCode, Toast.LENGTH_SHORT).show();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                if (canGetLocation()) {
//                    onIconClick(0);
                    Toast.makeText(this, getResources().getString(R.string.loc_perm_denied), Toast.LENGTH_SHORT).show();
                } else {
                    showSettingsAlert();
                }

            }
        } else {
            Toast.makeText(this, "CODE " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        //  stopService(mServiceIntent);
        super.onDestroy();
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setBuildingsEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);
        lastKnown = getLastKnownLocation();

        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng syd = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());

        // Zoom in the Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(syd, 5));
        Log.d("readyyy", "yes");

        // getLats();
        Call<SQList> getReportsModelCall = apiInterface.getsqlist(getAccessToken(this));
        getReportsModelCall.enqueue(new Callback<SQList>() {

            @Override
            public void onResponse(Call<SQList > call, Response<SQList> response) {
               
                Marker marker = null;
                mMap.clear();
                data.addAll(response.body().getId());
                Log.d("sqlistaaya",response.body().getStatus()+" "+data.size());

                for(int i=0;i<data.size();i++)
                {
                    double lat= data.get(i).getLat();
                    double lon=data.get(i).getLon();
                    LatLng sydney = new LatLng(lat, lon);
                    Log.d("sqlist",lon+" "+lat);

                    // LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                    //marker = new MarkerOptions().position(sydney).title(data.get(i).name);
                    marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Phone:"+data.get(i).getPhone()+"\n"+"Address:"+data.get(i).getAddress()));
                }

            }

            @Override
            public void onFailure(Call<SQList> call, Throwable t) {
                Log.d("sqlistaaya",t+" ");

            }


        });


/*
        LatLng sydney = new LatLng(34.34, 86.7);

        final MarkerOptions marker = new MarkerOptions().position(sydney).title("");
        //  Drawable i=;

            Marker m= mMap.addMarker(marker);}

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {





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

        if (

    MapsQurantine(permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnown = getLastKnownLocation();

            mMap.clear();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng sydney = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(sydney).title("Your current location");
            mMap.addMarker(marker);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        }


        // Add a marker in Sydney and move the camera*/

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                ActivityCompat.requestPermissions(MapQurantine.this,new String[]{permission.ACCESS_FINE_LOCATION},0);



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
    public boolean checkCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;


        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }
    public void getLats() {
        Log.d("ngomodel","in");



    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        String ngo= marker.getTitle();
        Log.d("ngonamer",ngo);
        Dialog dialog = createAlertDialog(MapQurantine.this, "Details", ngo, "Call", getResources().getString(R.string.ok));
//        dialog.setCancelable(false);
        // Setting Dialog Title
        dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCallPermission()) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ngo.substring(6, 16)));
                    startActivity(intent);

                    dialog.dismiss();
                }
                else
                    requestCallPermission();
            }
        });


        return true;
    }
    private void requestCallPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CALL_PHONE},
                CALL_PHONE_CODE
        );
    }

    private void getCurrentLocation() {

        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getCurrentLocation();
                                }
                            }, 1000);

                        } else {
                            currentLocation = new Location(location);
                            if(flag == 0){
                                flag = 1;
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.0f));
                            }
//                            Log.e("LOL", "OMG");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getCurrentLocation();
                                }
                            }, 1000);

//                            LOCATION_LAT = quarantineLocation.getLatitude();
//                            LOCATION_LON = quarantineLocation.getLongitude();

                        }
                    }
                }
        );
    }
}
