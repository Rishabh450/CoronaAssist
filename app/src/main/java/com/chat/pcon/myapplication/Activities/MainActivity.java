package com.chat.pcon.myapplication.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.chat.pcon.myapplication.Models.NodeModel;
import com.chat.pcon.myapplication.R;
import com.chat.pcon.myapplication.Utilities.GoogleApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_ID = 001;
    private LinearLayout iconGroceries;
    private LinearLayout iconMedicines;
    private LinearLayout locationLayout;
    private TextView nodeName;
    private List<NodeModel> nodesLocation;
    private ProgressBar nodeProgress;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListeners();
        getNearestNode();

    }

    private void setListeners() {
        iconGroceries.setOnClickListener(this);
        iconMedicines.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void init() {
        iconGroceries = findViewById(R.id.icon_groceries);
        iconMedicines = findViewById(R.id.icon_medicine);
        nodeName = findViewById(R.id.node_name);
        locationLayout = findViewById(R.id.node_location_layout);
        nodeProgress = findViewById(R.id.node_name_progress);
    }
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    private void getNearestNode() {

            // Execute some code after 1 second has passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if(isNetworkConnected())
                    new LocationStuff().execute();
                else{
                    handler.postDelayed(this,1000);
                }
            }
        }, 1000);

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private class LocationStuff extends AsyncTask<Void, Void, Void>{

        private Location currentLocation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nodeProgress.setVisibility(View.VISIBLE);
            nodeName.setText("");
            getCurrentLocation();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //get current location
            while(currentLocation==null){
            }

            nodesLocation = getNodesLocation();
            //get nodes location
            //punch cur and nod loc in list
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("LOC", String.valueOf(currentLocation));
            Double mDist = Double.MAX_VALUE;
            NodeModel minDistanceNode = null;
            for(NodeModel node: nodesLocation){
                node.distance = distance(currentLocation.getLatitude(),currentLocation.getLongitude(),
                        node.location.getLatitude(),node.location.getLongitude(),"K");
                if(mDist>node.distance){
                    mDist = node.distance;
                    minDistanceNode = node;
                }
            }
            nodeProgress.setVisibility(View.GONE);
            nodeName.setText(minDistanceNode.name);
            Log.e("TAG",minDistanceNode.name);

        }
        private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
            if ((lat1 == lat2) && (lon1 == lon2)) {
                return 0;
            }
            else {
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
        public List<NodeModel> getNodesLocation(){
            List<NodeModel> list = new ArrayList<>();
            list.add(new NodeModel("Bistupur",initLoc(23.981071, 85.363343)));
            list.add(new NodeModel("Adityapur",initLoc(23.981267, 85.361895)));
            return list;
        }
        Location initLoc(Double lat, Double lng){
            Location location = new Location("Provider");
            location.setLatitude(lat);
            location.setLongitude(lng);
            return location;
        }
        private void getCurrentLocation(){
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(
                            new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    if (location == null) {
                                        //select the node
                                    }else{
                                        currentLocation = location;

                                    }
                                }
                            }
                    );
                } else {
                    Toast.makeText(getApplicationContext(), "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent = null;
        switch (itemId){
            case R.id.icon_groceries:
                intent = new Intent(this, GroceriesActivity.class);
                startActivity(intent);
                break;
            case R.id.icon_medicine:
                intent = new Intent(this, MedicineActivity.class);
                startActivity(intent);
                break;
            case R.id.node_location_layout:
                startDialog();
                break;
        }
    }

    private void startDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_zone);
        setRecyclerViewInDialog(dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330/*height value*/, getResources().getDisplayMetrics()); for custom height value
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }
    void setRecyclerViewInDialog(Dialog dialog){
        ListView lview = dialog.findViewById(R.id.dialog_zone_lview);
        List<String> items=new ArrayList<>();
        items.add("Get Nearest Zone");
        for(int i=0;i<nodesLocation.size();i++){
            items.add(nodesLocation.get(i).name);
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        lview.setAdapter(itemsAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                if(tv.getText().toString().compareTo("Get Nearest Zone")==0){
                    getNearestNode();
                }else {
                    nodeName.setText(tv.getText().toString());
                }
                dialog.dismiss();
            }
        });
    }
}
