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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.MapsActivity;
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
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.district;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.is_ngo;
import static com.suvidha.Utilities.Utils.is_quarantine;
import static com.suvidha.Utilities.Utils.is_quarantined;
import static com.suvidha.Utilities.Utils.shopTypesMap;
import static com.suvidha.Utilities.Utils.state;

public class HomeFragment extends Fragment implements View.OnClickListener, MainActivity.NotifyFragment {
    private static final int PERMISSION_ID = 001;
    private Button iconGroceries;
    private Button iconRequestPass;
    private Button iconMilk;
    private Button iconBread;
    private Button iconGas;
    private Button iconWater;
    private RelativeLayout iconQuarentine;
    private RelativeLayout iconFoodSupply;
    private TextView services_txt;
    private TextView text_quarantine;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextInputEditText name, phone, address, st, end, authority, type;
    private CheckBox tnc;
    private Button cancel, register;
    ApiInterface apiInterface;
    private Location quarantineLocation;
    public Dialog dialog;
    public ProgressBar progressBar;
    private TextInputEditText spinner_zone, spinner_state, spinner_district;
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
        Log.d("accesstokengetting",getAccessToken(getContext()));
        iconWater = v.findViewById(R.id.icon_water);
        iconQuarentine = v.findViewById(R.id.icon_quarentine);
        text_quarantine = v.findViewById(R.id.txt_quarantine);
        iconFoodSupply = v.findViewById(R.id.icon_ngo);
        services_txt = v.findViewById(R.id.services_txt);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

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
        iconFoodSupply.setOnClickListener(this);
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
            case R.id.icon_ngo:{
                intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.icon_quarentine: {
                //open register quarentine dialog
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
                                getCurrentLocation();
                            }
                        } else {
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
                        } else {
                            quarantineLocation = new Location(location);
                            currentLocation = new Location(location);
                            dialog.dismiss();
                            Log.e("LOL", "OMG");
//                            LOCATION_LAT = quarantineLocation.getLatitude();
//                            LOCATION_LON = quarantineLocation.getLongitude();
                            if(is_quarantined == 0) {
                                createQuarentineDialog();
                            }else{
                                Intent intent;
                                intent = new Intent(getContext(), QuarantineActivity.class);
                                intent.putExtra("lat", (float) currentLocation.getLatitude());
                                intent.putExtra("lon", (float) currentLocation.getLongitude());
                                startActivity(intent);
                            }
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
    Dialog d;
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
        type = dialog.findViewById(R.id.quarantine_type);
        TextView termsandCondition = dialog.findViewById(R.id.terms_n_condition);
        termsandCondition.setText(getContext().getResources().getString(R.string.terms_and_condition));
        register.setEnabled(false);
        spinner_state = dialog.findViewById(R.id.quarantine_state);
        spinner_district = dialog.findViewById(R.id.quarantine_district);
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
        spinner_district.setText(district);
        spinner_state.setText(state);
        int dInd = Collections.binarySearch(mDistricts, initialDist);
        d = new Dialog(getContext());
        d.setContentView(R.layout.dialog_quarantine_type);
        Button home_q = d.findViewById(R.id.home_quarantine);
        Button special_q = d.findViewById(R.id.special_quarantine_center);
        home_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setText(home_q.getText().toString());
                d.dismiss();
            }
        });
        special_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setText(special_q.getText().toString());
                d.dismiss();
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open dialog
                d.show();

            }
        });

        end.setEnabled(false);
        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(st, end);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(end, null);
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
        if (sharedPrefManager.containsKey(SharedPrefManager.Key.USER_PHONE)) {
            phone.setText(sharedPrefManager.getString(SharedPrefManager.Key.USER_PHONE));
            phone.setEnabled(false);
        } else {
            phone.setEnabled(true);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etname = name.getText().toString().trim();
                String etphone = phone.getText().toString().trim();
                
                String etaddress = address.getText().toString().trim();
                String stDate = st.getText().toString().trim();
                String endDate = end.getText().toString().trim();
                String etAuthority = authority.getText().toString().trim();
                int et_type = (type.getText().toString().trim()).compareToIgnoreCase(getResources().getString(R.string.home_quarantine)) == 0 ? 1 : 2;
                if (type.getText().toString().length() == 0) {
                    et_type = 0;
                }

                boolean terms = tnc.isChecked();
                if (etname.length() != 0 && etphone.length() != 0 && etphone.length() == 10 && etaddress.length() != 0 && stDate.length() != 0 && endDate.length() != 0 && etAuthority.length() != 0 && terms && et_type != 0) {
//                    register quarantine
                    QuarantineModel model = new QuarantineModel(etname, etaddress, etphone, (float) quarantineLocation.getLatitude(), (float) quarantineLocation.getLongitude(), etAuthority, stDate, endDate, spinner_state.getText().toString(), spinner_district.getText().toString(), et_type);

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
                                        if (is_quarantined == 1) {

                                            Log.d("MainActivity", "started1" + is_quarantined);
                                            getActivity().startForegroundService(mServiceIntent);

                                        }

                                    } else {
                                        if (is_quarantined == 1) {
                                            Log.d("MainActivity", "started2");
                                            getActivity().startService(mServiceIntent);
                                        }
                                    }
                                }

                            } else {
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
                    if (et_type == 0) {
                        type.setError(getResources().getString(R.string.select_type));
                    }
                    if (!tnc.isChecked()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.accept_tnc), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), getResources().getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTimePicker(TextInputEditText et, TextInputEditText et_end) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (et_end != null) {
                            et_end.setEnabled(true);
                        }
                        et.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH, 20);
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
        if (is_quarantined == 1) {
            text_quarantine.setText(getResources().getString(R.string.Quarantine));
        } else {
            text_quarantine.setText(getResources().getString(R.string.register_quarantine));
        }
        if(is_quarantine==1){
            iconQuarentine.setVisibility(View.VISIBLE);
        }else {
            iconQuarentine.setVisibility(View.GONE);
        }
        if(is_ngo == 1){
            iconFoodSupply.setVisibility(View.VISIBLE);
        }else{
            iconFoodSupply.setVisibility(View.GONE);
        }
        services_txt.setText(getResources().getString(R.string.services) + " " + district);
    }
}
