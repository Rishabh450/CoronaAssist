package com.suvidha.Fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.MapsActivity;
import com.suvidha.Activities.MyPassActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Activities.RegisterQuarantineActivity;
import com.suvidha.Activities.ShopsActivity;
import com.suvidha.Adapters.CategoryAdapter;
import com.suvidha.Adapters.SupportAdapter;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.HomeIconModel;
import com.suvidha.Models.QuarantineModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.LiveLocationService;
import com.suvidha.Utilities.SharedPrefManager;
import com.suvidha.Utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.district;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.home_icons;
import static com.suvidha.Utilities.Utils.is_ngo;
import static com.suvidha.Utilities.Utils.is_pass;
import static com.suvidha.Utilities.Utils.is_quarantine;
import static com.suvidha.Utilities.Utils.is_quarantined;
import static com.suvidha.Utilities.Utils.is_shopper;


public class HomeFragment extends Fragment implements View.OnClickListener, MainActivity.NotifyFragment, SupportAdapter.HomeCallback {
    private static final int PERMISSION_ID = 001;
    private static final int REGISTER_REQUEST_CODE = 5;
    private TextView services_txt;
    private FusedLocationProviderClient mFusedLocationClient;
    ApiInterface apiInterface;

    public Dialog dialog;
    public ProgressBar progressBar;

    private RecyclerView gridHome;
    private SupportAdapter mAdapter;
    List<HomeIconModel> iconList = new ArrayList<>();

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        setHomeGrid();
        intialiseRetrofit();
        setListeners();
        notifyDataLoaded();
        return v;
    }


    private void init(View v) {
        services_txt = v.findViewById(R.id.services_txt);
        gridHome = v.findViewById(R.id.home_grid);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

    }

    private void setHomeGrid() {
        gridHome.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new SupportAdapter(this, iconList);
        gridHome.setAdapter(mAdapter);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void setListeners() {
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent = null;

    }

    private void getCurrentLocation() {
        if (dialog == null) {
            dialog = createProgressDialog(getContext(), getContext().getResources().getString(R.string.please_wait));
        }
        progressBar = dialog.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
        staticProgress.setVisibility(View.GONE);
        dialog.show();
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {

                            TextView msg = dialog.findViewById(R.id.progress_msg);
                            msg.setText(getContext().getResources().getString(R.string.try_again));

                            progressBar = dialog.findViewById(R.id.progress_bar);
                            progressBar.setVisibility(View.INVISIBLE);
                            ImageView staticProgress = dialog.findViewById(R.id.static_progress);
                            staticProgress.setVisibility(View.VISIBLE);
                            staticProgress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getCurrentLocation();
                                }
                            });

                        } else {
                            currentLocation = new Location(location);
                            dialog.dismiss();
                            Log.e("LOL", "OMG");
//                            LOCATION_LAT = quarantineLocation.getLatitude();
//                            LOCATION_LON = quarantineLocation.getLongitude();
                            Intent intent;
                            intent = new Intent(getContext(), QuarantineActivity.class);
                            intent.putExtra("lat", (float) currentLocation.getLatitude());
                            intent.putExtra("lon", (float) currentLocation.getLongitude());
                            startActivity(intent);

                        }
                    }
                }
        );
    }


    private void createQuarentineDialog() {

        setDialog(dialog);

        dialog.show();

    }


    private void setDialog(Dialog dialog) {

    }


    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestLocationPermissions() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActivityCompat.requestPermissions(
                mainActivity,
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

            lm = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

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
        Dialog dialog = createAlertDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.turn_on_gps), getResources().getString(R.string.back), getResources().getString(R.string.ok));
//        dialog.setCancelable(false);
        // Setting Dialog Title
        dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("LOL", "WTH");
        if (requestCode == LOCATION_PERMISSION_CODE) {
            Toast.makeText(getContext(), "WTH " + requestCode, Toast.LENGTH_SHORT).show();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                if (canGetLocation()) {
                    onIconClick(0);
                    Toast.makeText(getContext(), getResources().getString(R.string.loc_perm_denied), Toast.LENGTH_SHORT).show();
                } else {
                    showSettingsAlert();
                }

            }
        } else {
            Toast.makeText(getContext(), "CODE " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void notifyDataLoaded() {
        Log.e("LOL", String.valueOf(is_quarantined));
        iconList.clear();
        iconList.addAll(home_icons);
        int pos = 0;
        if (is_quarantine == 0) {
            iconList.remove(0);
            pos++;
        }
        if (is_pass == 0) {
            iconList.remove(1 - pos);
            pos++;
        }
        if (is_shopper == 0) {
            iconList.remove(2 - pos);
            pos++;
        }
        if (is_ngo == 0) {
            iconList.remove(3 - pos);
        }
        mAdapter.notifyDataSetChanged();

        services_txt.setText(getResources().getString(R.string.services) + " " + district);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                notifyDataLoaded();
                Toast.makeText(getContext(), getResources().getString(R.string.successfully_registered_for_quarantine), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onIconClick(int pos) {
        Intent intent = null;
        Log.e("LOL", String.valueOf(pos));
        switch (pos) {
            case 0: {
                if (checkLocationPermission()) {
                    //first get current location as quarantine location
                    //then open dialog
                    if (canGetLocation()) {
                        if (is_quarantined == 1) {
                            Location location = getLastKnownLocation();
                            if (location != null) {
                                Log.d("lastknown", location.getLatitude() + " " + location.getLongitude());

                                intent = new Intent(getContext(), QuarantineActivity.class);
                                intent.putExtra("lat", (float) location.getLatitude());
                                intent.putExtra("lon", (float) location.getLongitude());

                                startActivity(intent);
                            } else {
                                if (checkLocationPermission()) {
                                    //first get current location as quarantine location
                                    //then open dialog
                                    if (canGetLocation()) {
                                        getCurrentLocation();

                                    } else {
                                        showSettingsAlert();
                                    }

                                } else {
                                    requestLocationPermissions();
                                    Toast.makeText(getContext(), getResources().getString(R.string.cant_get_location), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            startActivityForResult(new Intent(getContext(), RegisterQuarantineActivity.class), REGISTER_REQUEST_CODE);
                        }
                    } else {
                        showSettingsAlert();
                    }

                } else {
                    requestLocationPermissions();
//                    Toast.makeText(getContext(), "You don't have location permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 1: {
                intent = new Intent(getContext(), MyPassActivity.class);
                startActivity(intent);
                break;
            }
            case 2:
                intent = new Intent(getContext(), ShopsActivity.class);
                startActivity(intent);
                break;
            case 3: {
                intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
