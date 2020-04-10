package com.suvidha.Fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Activities.FAQActivity;
import com.suvidha.Activities.LoginActivity;
import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.MyPassActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Activities.ShopsActivity;
import com.suvidha.Models.GeneralModel;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.is_quarantined;
import static com.suvidha.Utilities.Utils.shopTypesMap;

public class HomeFragment extends Fragment implements View.OnClickListener, MainActivity.NotifyFragment {
    private static final int PERMISSION_ID = 001;
    private Button iconGroceries;
    private Button iconRequestPass;
    private Button iconMilk;
    private Button iconBread;
    private Button iconGas;
    private Button iconWater;
    private Button iconQuarentine;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextInputEditText name, phone, address, st, end, authority;
    private CheckBox tnc;
    private Button cancel, register;
    ApiInterface apiInterface;
    private Location quarantineLocation;
    public Dialog dialog;
    public ProgressBar progressBar;
    private Spinner spinner_zone, spinner_state, spinner_district;
    private String mSelectedState, mSelectedDistrict;
    private List<String> mDistricts;
    private int counter;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        intialiseRetrofit();
        setListeners();
        notifyDataLoaded();
        return v;
    }

    private void init(View v) {
        iconGroceries = v.findViewById(R.id.icon_groceries);
        iconRequestPass = v.findViewById(R.id.icon_request_passes);
        iconMilk = v.findViewById(R.id.icon_milk_and_dairy);
        iconBread = v.findViewById(R.id.icon_bread);
        iconGas = v.findViewById(R.id.icon_gas);
        iconWater = v.findViewById(R.id.icon_water);
        iconQuarentine = v.findViewById(R.id.icon_quarentine);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        v.findViewById(R.id.icon_faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FAQActivity.class));

            }
        });
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void setListeners() {
        iconGroceries.setOnClickListener(this);
        iconRequestPass.setOnClickListener(this);
        iconMilk.setOnClickListener(this);
        iconBread.setOnClickListener(this);
        iconGas.setOnClickListener(this);
        iconWater.setOnClickListener(this);
        iconQuarentine.setOnClickListener(this);
    }
    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager)
                getActivity(). getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new  HomeFragment.MyLocationListener();
        if (checkLocationPermission()) {
            //first get current location as quarantine location
            //then open dialog
            if (canGetLocation()) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,1000, 0, locationListener);
            } else {
                showSettingsAlert();
            }

        } else {
            requestLocationPermissions();
//                    Toast.makeText(getContexgetLocationUpdatest(), "You don't have location permission", Toast.LENGTH_SHORT).show();
        }

    }
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if(loc!=null) {
                double lat=loc.getLatitude();
                double lon=loc.getLongitude();
                Intent intent = new Intent(getContext(), QuarantineActivity.class);
                intent.putExtra("lat",(float)lat);
                intent.putExtra("lon",(float)lon);
                startActivity(intent);


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
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent = null;
        switch (itemId) {
            case R.id.icon_request_passes:
                intent = new Intent(getContext(), MyPassActivity.class);
                startActivity(intent);
                break;
            case R.id.icon_quarentine: {
                //open register quarentine dialog
                if (checkLocationPermission()) {
                    //first get current location as quarantine location
                    //then open dialog
                    if (canGetLocation()) {
                        if (is_quarantined == 1) {
                            Location location=getLastKnownLocation();
                            if(location!=null) {
                                Log.d("lastknown",location.getLatitude()+" "+location.getLongitude());

                                intent = new Intent(getContext(), QuarantineActivity.class);
                                intent.putExtra("lat",(float)location.getLatitude());
                                intent.putExtra("lon",(float)location.getLongitude());
                                startActivity(intent);
                            }
                            else
                            {
                                getLocationUpdates();
                            }
                        }else {
                            getCurrentLocation();
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
            default:
                Button b = (Button) v;
                intent = new Intent(getContext(), ShopsActivity.class);
                intent.putExtra("type", shopTypesMap.get(itemId));
                intent.putExtra("type_name", b.getText().toString());
                startActivity(intent);
        }
    }

    private void getCurrentLocation() {
        if (dialog == null) {
            dialog = createProgressDialog(getContext(), "Please wait");
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
                            msg.setText("Try Again");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
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
                                }
                            }, 500);
                            Toast.makeText(getContext(), getResources().getString(R.string.cant_get_location), Toast.LENGTH_LONG).show();
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 22.8046, 86.2029);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);
                        } else {
                            quarantineLocation = new Location(location);
                            currentLocation = new Location(location);
                            dialog.dismiss();
                            Log.e("LOL", "OMG");
//                            LOCATION_LAT = quarantineLocation.getLatitude();
//                            LOCATION_LON = quarantineLocation.getLongitude();

                            createQuarentineDialog();

                        }
                    }
                }
        );
    }
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }
    private void createQuarentineDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_quarantine);
        setDialog(dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    private void setDialog(Dialog dialog) {
        name = dialog.findViewById(R.id.quarantine_register_name);
        name.setText(SharedPrefManager.getInstance(getContext()).getString(SharedPrefManager.Key.USER_NAME));
        phone = dialog.findViewById(R.id.quarantine_mobile);
        address = dialog.findViewById(R.id.quarantine_address);
        tnc = dialog.findViewById(R.id.tnc);
        cancel = dialog.findViewById(R.id.quarantine_cancel);
        st = dialog.findViewById(R.id.quarantine_start_date);
        end = dialog.findViewById(R.id.quarantine_end_date);
        register = dialog.findViewById(R.id.quarantine_register);
        authority = dialog.findViewById(R.id.quarantine_authority);
        TextView termsandCondition = dialog.findViewById(R.id.terms_n_condition);
        termsandCondition.setText(getContext().getResources().getString(R.string.terms_and_condition));
        register.setEnabled(false);
        spinner_state = dialog.findViewById(R.id.register_state);
        spinner_district = dialog.findViewById(R.id.register_district);
        mDistricts = new ArrayList<>();
        counter = 0;

        Utils.parseJson(getContext());

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
        String initialState = sharedPrefManager.getString(SharedPrefManager.Key.STATE_KEY, "");
        String initialDist = sharedPrefManager.getString(SharedPrefManager.Key.DISTRICT_KEY, "");

        Object states[] = Utils.mStateDist.keySet().toArray();
        Arrays.sort(states);
        int stInd = Arrays.binarySearch(states, initialState);
        ArrayAdapter aa1 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, states);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(aa1);
        spinner_state.setSelection(stInd);
        mSelectedState = spinner_state.getSelectedItem().toString();
        mDistricts = Utils.mStateDist.get(mSelectedState);
        int dInd = Collections.binarySearch(mDistricts, initialDist);

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedState = states[position].toString();
                mDistricts = Utils.mStateDist.get(mSelectedState);
                ArrayAdapter aa2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, mDistricts);
                aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_district.setAdapter(aa2);
                if (counter == 0)
                    spinner_district.setSelection(dInd);
                counter++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        end.setEnabled(false);
        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(st,end);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(end,null);
            }
        });
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tnc.isChecked()) {
                    tnc.setChecked(true);
                    register.setEnabled(true);
                } else {
                    tnc.setChecked(false);
                    register.setEnabled(false);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etname = name.getText().toString().trim();
                String etphone = phone.getText().toString().trim();
                String etaddress = address.getText().toString().trim();
                String stDate = st.getText().toString().trim();
                String endDate = end.getText().toString().trim();
                String etAuthority = authority.getText().toString().trim();
                mSelectedState = spinner_state.getSelectedItem().toString();
                mSelectedDistrict = spinner_district.getSelectedItem().toString();
                boolean terms = tnc.isChecked();
                if (etname.length() != 0 && etphone.length() != 0 && etphone.length() == 10 && etaddress.length() != 0 && stDate.length() != 0 && endDate.length() != 0 && etAuthority.length() != 0 && terms && mSelectedDistrict.length() != 0 && mSelectedState.length() != 0) {
//                    register quarantine
                    QuarantineModel model = new QuarantineModel(etname, etaddress, etphone, (float) quarantineLocation.getLatitude(), (float) quarantineLocation.getLongitude(), etAuthority, stDate, endDate, mSelectedState, mSelectedDistrict);

                    Call<GeneralModel> registerResult = apiInterface.register_quarantine(getAccessToken(getContext()), model);
                    registerResult.enqueue(new Callback<GeneralModel>() {
                        @Override
                        public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                            if (response.body().status == 200) {
                                //success
                                is_quarantined = 1;

                                notifyDataLoaded();
                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.IS_QUARANTINE, 1);

                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.QUARENTINE_LAT_KEY, (float) quarantineLocation.getLatitude());

                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.QUARENTINE_LAT_KEY, (float) quarantineLocation.getLatitude());
                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.QUARENTINE_LON_KEY, (float) quarantineLocation.getLongitude());
                                Toast.makeText(getContext(), getResources().getString(R.string.successfully_registered_for_quarantine), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dialog.dismiss();

                                LiveLocationService mYourService = new LiveLocationService();
                                Intent mServiceIntent = new Intent(getContext(), mYourService.getClass());
                                if (!isMyServiceRunning(mYourService.getClass())) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        if(is_quarantined==1) {

                                            Log.d("MainActivity","started1"+is_quarantined);
                                            getActivity().startForegroundService(mServiceIntent);

                                        }

                                    } else {
                                        if(is_quarantined==1) {
                                            Log.d("MainActivity","started2");
                                            getActivity().startService(mServiceIntent);
                                        }
                                    }
                                }

                            } else {
                                Log.e("heey",call.toString()+" -- "+response.errorBody().toString());
                                Toast.makeText(getContext(), getResources().getString(R.string.failed_to_register_quarantine), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralModel> call, Throwable t) {
                            Log.e("heey", t.getMessage());
                            Toast.makeText(getContext(), getResources().getString(R.string.failed_to_register_quarantine), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    if (etname.length() == 0) {
                        name.setError(getResources().getString(R.string.name_cannot_be_empty));
                    }
                    if (etaddress.length() == 0) {
                        address.setError(getResources().getString(R.string.address_cannot_be_empty));
                    }
                    if (etphone.length() != 10) {
                        phone.setError(getResources().getString(R.string.enter_valid_phno));
                    }
                    if (etAuthority.length() == 0) {
                        authority.setError(getResources().getString(R.string.authority_cannot_be_empty));
                    }
                    if (stDate.length() == 0) {
                        st.setError(getResources().getString(R.string.pick_start_date));
                    }
                    if (endDate.length() == 0) {
                        end.setError(getResources().getString(R.string.pick_end_date));
                    }
                    if (!tnc.isChecked()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.accept_tnc), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showTimePicker(TextInputEditText et,TextInputEditText et_end) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if(et_end!=null){
                            et_end.setEnabled(true);
                        }
                        et.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH,20);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        // Setting Dialog Title
        alertDialog.setTitle("Error!");

        // Setting Dialog Message
        alertDialog.setMessage("Please Turn on your GPS ");

        // On pressing Settings button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
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
                    getCurrentLocation();
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
                if (getActivity(). checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (is_quarantined == 1) {
            iconQuarentine.setText(getResources().getString(R.string.Quarantine));
        } else {
            iconQuarentine.setText(getResources().getString(R.string.register_quarantine));
        }
    }
}
