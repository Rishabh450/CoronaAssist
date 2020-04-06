package com.suvidha.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.suvidha.Adapters.QuarantineAdapter;
import com.suvidha.Fragments.HistoryFragment;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.GetReportsModel;
import com.suvidha.Models.ReportModel;
import com.suvidha.R;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.suvidha.Utilities.Utils.CAMERA_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;


public class QuarantineActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 5;
    private FusedLocationProviderClient mFusedLocationClient;
    private Toolbar toolbar;
    private AppBarLayout toolbar_layout;
    ApiInterface apiInterface;
    private int location_error=0;
    private static final int THRESHOLD_DIST = 200;
    private double lat,lon;
    private Button reportBtn;
    private RecyclerView rview;
    private QuarantineAdapter mAdapter;
    private List<ReportModel> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarantine);
        init();
        setRecyclerView();
        intialiseRetrofit();
        getReports();
        lat = getIntent().getDoubleExtra("lat",0);
        lon = getIntent().getDoubleExtra("lon",0);
        setToolbar();
        calDiffDist();


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
        Call<GetReportsModel> getReportsModelCall = apiInterface.get_report(getAccessToken(this));
        getReportsModelCall.enqueue(new Callback<GetReportsModel>() {
            @Override
            public void onResponse(Call<GetReportsModel> call, Response<GetReportsModel> response) {
//                dialog.dismiss();
                data.clear();
                data.addAll(response.body().id);
                data.sort(new TimestampSorter());
                mAdapter.notifyDataSetChanged();
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
                Toast.makeText(QuarantineActivity.this, getResources().getString(R.string.cannot_get_your_reports), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        rview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new QuarantineAdapter(this,data);
        rview.setAdapter(mAdapter);

    }

    private void calDiffDist() {
        float qlat= SharedPrefManager.getInstance(this).getFloat(SharedPrefManager.Key.QUARENTINE_LAT_KEY,0);
        float qlon= SharedPrefManager.getInstance(this).getFloat(SharedPrefManager.Key.QUARENTINE_LON_KEY,0);
        double d = distance((double) qlat,(double) qlon,currentLocation.getLatitude(),currentLocation.getLongitude(),"K")*1000;
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
        rview = findViewById(R.id.quarantine_rview);
        toolbar = findViewById(R.id.default_toolbar);
        toolbar_layout = findViewById(R.id.main_app_bar);
        reportBtn = findViewById(R.id.report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCameraPermission()){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    startActivityForResult(intent,CAMERA_REQUEST);
                }else{
                    requestCameraPermission();
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
//            Dialog dialog = createProgressDialog(getApplicationContext(),getResources().getString(R.string.please_wait));
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            ReportModel model = new ReportModel(encoded,(float) currentLocation.getLatitude(),(float)currentLocation.getLongitude(),"hvjhvjh",location_error);
            Call<GeneralModel> call = apiInterface.send_report(getAccessToken(getApplicationContext()),model);
            call.enqueue(new Callback<GeneralModel>() {
                @Override
                public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
//                    dialog.dismiss();
                    getReports();
                    Dialog alertDialog = createAlertDialog(QuarantineActivity.this,getResources().getString(R.string.successful),getResources().getString(R.string.submitter_successfully),"",getResources().getString(R.string.ok));
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
//                    dialog.dismiss();
                    Toast.makeText(QuarantineActivity.this, getResources().getString(R.string.failed_to_submit_report), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
        }
    }
    public class TimestampSorter implements Comparator<ReportModel>
    {
        DateFormat f = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
        @Override
        public int compare(ReportModel o1, ReportModel o2) {
            try {
                if(f.parse(o2.report_time).before(f.parse(o1.report_time))){
                    return 10;
                }else{
                    return -10;
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
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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
        }
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