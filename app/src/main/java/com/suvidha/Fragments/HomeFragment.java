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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Activities.LoginActivity;
import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.MapQurantine;
import com.suvidha.Activities.MapsActivity;
import com.suvidha.Activities.MyPassActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Activities.RegisterQuarantineActivity;
import com.suvidha.Activities.ShopsActivity;
import com.suvidha.Adapters.CategoryAdapter;
import com.suvidha.Adapters.SupportAdapter;
import com.suvidha.Models.CityModel;
import com.suvidha.Models.DeliveryAddressModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.HomeIconModel;
import com.suvidha.Models.QuarantineModel;
import com.suvidha.Models.SectorModel;
import com.suvidha.Models.SubZoneModel;
import com.suvidha.Models.ZonesModel;
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
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.city;
import static com.suvidha.Utilities.Utils.clearLoginSession;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.district;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.home_icons;
import static com.suvidha.Utilities.Utils.is_delivery;
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

    Map<String,List<ZonesModel>> mCity = new HashMap<>();
    Map<String,List<SubZoneModel>> mZone = new HashMap<>();
    Map<String,List<SectorModel>> mSubzone = new HashMap<>();
    Map<String,List<String>> mSector = new HashMap<>();
    List<ZonesModel> zones;
    List<SubZoneModel> subzones;
    List<SectorModel> sectors;
    List<String> areas;
    String mSelectedCity,mSelectedZone,mSelectedSubzone,mSelectedSector,mSelectedArea;
    Spinner city_spinner,zone_spinner,subzone_spinner,sector_spinner,area_spinner;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        intialiseRetrofit();
        setHomeGrid();
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
                //open dialog to select city
                if(is_delivery == 0)
                    createAddressDialog();
                else{
                    intent = new Intent(getContext(), ShopsActivity.class);
                    startActivity(intent);
                }

                break;
            case 3: {
                intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                break;
            }
            case 4: {
                intent = new Intent(getContext(), MapQurantine.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void createAddressDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_address);
        setAddressDialog(dialog);
        dialog.show();
    }

    void setAddressDialog(Dialog dialog){
        city_spinner = dialog.findViewById(R.id.address_city);
        zone_spinner = dialog.findViewById(R.id.address_zone);
        subzone_spinner = dialog.findViewById(R.id.address_subzone);
        sector_spinner = dialog.findViewById(R.id.address_sector);
        area_spinner = dialog.findViewById(R.id.address_area);



        //set data
        for(CityModel c: city){
            mCity.put(c.name,c.zone);
            for(ZonesModel z: c.zone){
                mZone.put(z.name,z.subzone);
                for(SubZoneModel sub:z.subzone){
                    mSubzone.put(sub.name,sub.sector);
                    for (SectorModel s: sub.sector)
                        mSector.put(s.name,s.area);
                }
            }
        }
        //set spinner
        setCitySpinner();
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set delivery address
                setDeliveryAddress(dialog);
            }
        });

    }

    private void setDeliveryAddress(Dialog dialog) {
        DeliveryAddressModel model = new DeliveryAddressModel(mSelectedCity,mSelectedZone,mSelectedSubzone,mSelectedSector,mSelectedArea);
        Call<GeneralModel> call = apiInterface.set_delivery_address(getAccessToken(getContext()),model);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                if(response.body().status != 302){
                    if(response.body().status == 201){
                        Log.e("Home Fragment",response.body().id);
                        dialog.dismiss();
                        Intent intent = new Intent(getContext(), ShopsActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else {
                    signOut();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        MainActivity mainActivity = (MainActivity) getActivity();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        googleSignInClient.signOut().addOnCompleteListener(mainActivity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //clear data
                clearLoginSession(mainActivity);

            }
        });
    }
    private void setCitySpinner() {
        final Object cities[] = mCity.keySet().toArray();
        Arrays.sort(cities);
        ArrayAdapter aa1 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, cities);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city_spinner.setAdapter(aa1);
        mSelectedCity =city_spinner.getSelectedItem().toString();
        zones = mCity.get(mSelectedCity);
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedCity = cities[i].toString();
                updateZoneSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateZoneSpinner() {
        zones = mCity.get(mSelectedCity);
        List<String> d = new ArrayList<>();
        for(ZonesModel model:zones)
        {
            d.add(model.name);
        }
        ArrayAdapter aa2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, d);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zone_spinner.setAdapter(aa2);
        mSelectedZone = zone_spinner.getSelectedItem().toString();
        zone_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedZone = zones.get(i).name;
                Log.e("Register",mSelectedZone);
                updateSubZoneSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateSubZoneSpinner() {
        subzones = mZone.get(mSelectedZone);
        List<String> d = new ArrayList<>();
        for(SubZoneModel model:subzones)
        {
            d.add(model.name);
        }
        ArrayAdapter aa2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, d);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subzone_spinner.setAdapter(aa2);
        mSelectedSubzone = subzone_spinner.getSelectedItem().toString();
        subzone_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedSubzone = subzones.get(i).name;
                Log.e("Register",mSelectedSubzone);
                updateSectorSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateSectorSpinner() {
        sectors = mSubzone.get(mSelectedSubzone);
        List<String> d = new ArrayList<>();
        for(SectorModel model:sectors)
        {
            d.add(model.name);
        }
        ArrayAdapter aa2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, d);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sector_spinner.setAdapter(aa2);
        mSelectedSector = sector_spinner.getSelectedItem().toString();
        sector_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedSector = sectors.get(i).name;
                Log.e("Register",mSelectedSector);
                updateAreaSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateAreaSpinner() {
        areas = mSector.get(mSelectedSector);
        List<String> d = new ArrayList<>();
        for(String model:areas)
        {
            d.add(model);
        }
        ArrayAdapter aa2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, d);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area_spinner.setAdapter(aa2);
        mSelectedArea = area_spinner.getSelectedItem().toString();
        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedArea = areas.get(i);
                Log.e("Register",mSelectedArea);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
