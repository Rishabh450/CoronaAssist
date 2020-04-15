package com.suvidha.Activities;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.QuarantineModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.LiveLocationService;
import com.suvidha.Utilities.SharedPrefManager;
import com.suvidha.Utilities.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.currentLocation;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.is_quarantined;
import static com.suvidha.Utilities.Utils.special_q_list;
import static com.suvidha.Utilities.Utils.state;
import static com.suvidha.Utilities.Utils.district;


public class RegisterQuarantineActivity extends AppCompatActivity {
    private Button cancel, register;
    private TextInputEditText et_name, et_phone, et_address, et_st, et_end, et_authority, et_type, et_sec_code, et_state, et_district;
    private TextView termsandCondition;
    private CheckBox tnc;
    public ProgressBar progressBar;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location quarantineLocation;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_quarantine);
        init();
        setInitialData();
        intialiseRetrofit();
    }

    void init() {
        et_name = findViewById(R.id.quarantine_register_name);
        et_name.setText(SharedPrefManager.getInstance(this).getString(SharedPrefManager.Key.USER_NAME));
        et_phone = findViewById(R.id.quarantine_mobile);
        et_address = findViewById(R.id.quarantine_address);
        termsandCondition = findViewById(R.id.terms_n_condition);
        tnc = findViewById(R.id.tnc);

        et_st = findViewById(R.id.quarantine_start_date);
        et_end = findViewById(R.id.quarantine_end_date);

        et_authority = findViewById(R.id.quarantine_authority);
        et_type = findViewById(R.id.quarantine_type);

        termsandCondition.setText(getResources().getString(R.string.terms_and_condition));

        et_state = findViewById(R.id.quarantine_state);
        et_district = findViewById(R.id.quarantine_district);
        et_sec_code = findViewById(R.id.quarantine_security_code);
        et_address.setEnabled(false);
        et_end.setEnabled(false);
        et_phone.setEnabled(false);

        cancel = findViewById(R.id.quarantine_cancel);
        register = findViewById(R.id.quarantine_register);
        register.setEnabled(false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    void setInitialData() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        et_district.setText(district);
        et_state.setText(state);

        et_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open dialog
                setTypeDialog();


            }
        });
        setAddress();
        setDate();
        setTnc();
        et_phone.setText(sharedPrefManager.getString(SharedPrefManager.Key.USER_PHONE));
        onRegister();
    }

    private void onRegister() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                    Toast.makeText(getContext(), "You don't have location permission", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    Dialog dialog;

    private void getCurrentLocation() {
        if (dialog == null) {
            dialog = createProgressDialog(this, this.getResources().getString(R.string.please_wait));
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
                            msg.setText(getResources().getString(R.string.try_again));
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
                            dialog.dismiss();
                            completeRegistration();

                        }
                    }
                }
        );
    }

    private void completeRegistration() {
        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String sec_code = et_sec_code.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String stDate = et_st.getText().toString().trim();
        String endDate = et_end.getText().toString().trim();
        String authority = et_authority.getText().toString().trim();
        int type = (et_type.getText().toString().trim()).compareToIgnoreCase(getResources().getString(R.string.home_quarantine)) == 0 ? 1 : 2;
        if (et_type.getText().toString().length() == 0) {
            type = 0;
        }

        boolean terms = tnc.isChecked();
        if (name.length() != 0 && phone.length() != 0 && phone.length() == 10 && address.length() != 0 && stDate.length() != 0 && endDate.length() != 0 && authority.length() != 0 && terms && type != 0 && sec_code.length() != 0) {
//                    register quarantine
            QuarantineModel model = new QuarantineModel(name, address, phone, (float) quarantineLocation.getLatitude(), (float) quarantineLocation.getLongitude(), authority, stDate, endDate, et_state.getText().toString(), et_district.getText().toString(), type, sec_code);

            Call<GeneralModel> registerResult = apiInterface.register_quarantine(getAccessToken(this), model);
            registerResult.enqueue(new Callback<GeneralModel>() {
                @Override
                public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                    if (response.body().status != 400) {
                        if (response.body().status == 200) {
                            //success
                            is_quarantined = 1;
                            SharedPrefManager.getInstance(RegisterQuarantineActivity.this).put(SharedPrefManager.Key.IS_QUARANTINE, 1);

                            SharedPrefManager.getInstance(RegisterQuarantineActivity.this).put(SharedPrefManager.Key.QUARENTINE_LAT_KEY, (float) quarantineLocation.getLatitude());

                            SharedPrefManager.getInstance(RegisterQuarantineActivity.this).put(SharedPrefManager.Key.QUARENTINE_LAT_KEY, (float) quarantineLocation.getLatitude());
                            SharedPrefManager.getInstance(RegisterQuarantineActivity.this).put(SharedPrefManager.Key.QUARENTINE_LON_KEY, (float) quarantineLocation.getLongitude());
                            Toast.makeText(RegisterQuarantineActivity.this, getResources().getString(R.string.successfully_registered_for_quarantine), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            dialog.dismiss();

                            LiveLocationService mYourService = new LiveLocationService();
                            Intent mServiceIntent = new Intent(RegisterQuarantineActivity.this, mYourService.getClass());
                            if (!isMyServiceRunning(mYourService.getClass())) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    if (is_quarantined == 1) {

                                        Log.d("MainActivity", "started1" + is_quarantined);
                                        startForegroundService(mServiceIntent);

                                    }

                                } else {
                                    if (is_quarantined == 1) {
                                        Log.d("MainActivity", "started2");
                                        startService(mServiceIntent);
                                    }
                                }
                            }
                            setResult(RESULT_OK, null);
                            finish();

                        } else {
                            Toast.makeText(RegisterQuarantineActivity.this, getResources().getString(R.string.failed_to_register_quarantine)+response.body().status, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterQuarantineActivity.this, getResources().getString(R.string.security_code_incorrect), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeneralModel> call, Throwable t) {
                    Log.e("heey", t.getMessage());
                    Toast.makeText(RegisterQuarantineActivity.this, getResources().getString(R.string.failed_to_register_quarantine), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            if (name.length() == 0) {
                et_name.setError(getResources().getString(R.string.name_cannot_be_empty));
            }
            if (address.length() == 0) {
                et_address.setError(getResources().getString(R.string.address_cannot_be_empty));
            }
            if (phone.length() != 10) {
                et_phone.setError(getResources().getString(R.string.enter_valid_phno));
            }
            if (authority.length() == 0) {
                et_authority.setError(getResources().getString(R.string.authority_cannot_be_empty));
            }
            if (stDate.length() == 0) {
                et_st.setError(getResources().getString(R.string.pick_start_date));
            }
            if (endDate.length() == 0) {
                et_end.setError(getResources().getString(R.string.pick_end_date));
            }
            if (type == 0) {
                et_type.setError(getResources().getString(R.string.select_type));
            }
            if (!tnc.isChecked()) {
                Toast.makeText(this, getResources().getString(R.string.accept_tnc), Toast.LENGTH_SHORT).show();
            }
            if (sec_code.length() == 0) {
                et_sec_code.setError(getResources().getString(R.string.sec_error));
            }
            Toast.makeText(this, getResources().getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show();
        }
    }

    public void showSettingsAlert() {
        Dialog dialog = createAlertDialog(this, getResources().getString(R.string.error), getResources().getString(R.string.turn_on_gps), getResources().getString(R.string.back), getResources().getString(R.string.ok));
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
        dialog.findViewById(R.id.quarantine_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setTnc() {
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
    }

    private void setDate() {
        et_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(et_st, et_end);
            }
        });
        et_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(et_end, null);
            }
        });

    }

    private void setAddress() {
        et_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_type.getText().toString().compareTo(getResources().getString(R.string.home_quarantine)) != 0 && special_q_list.size() != 0) {
                    Log.e("LOL", "LOL");
                    Dialog dialog = new Dialog(RegisterQuarantineActivity.this);
                    dialog.setContentView(R.layout.dialog_state_quarantine);
                    ListView lView = dialog.findViewById(R.id.state_q_lview);
                    final List<String> values = new ArrayList<>();
                    for (int i = 0; i < special_q_list.size(); i++) {
                        values.add(special_q_list.get(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterQuarantineActivity.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);
                    lView.setAdapter(adapter);
                    dialog.show();

                    lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            et_address.setText(values.get(i));
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
    }

    private void setTypeDialog() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_quarantine_type);
        Button home_q = d.findViewById(R.id.home_quarantine);
        Button special_q = d.findViewById(R.id.special_quarantine_center);
        home_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_address.setText("");
                et_type.setText(home_q.getText().toString());
                et_address.setEnabled(true);
                et_address.setFocusableInTouchMode(true);
                et_address.setFocusable(true);
                d.dismiss();
            }
        });
        special_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_address.setText("");
                et_type.setText(special_q.getText().toString());

                if (special_q_list.size() == 0) {
                    Log.e("TAG", "SPECIALLOL");
                    et_address.setEnabled(true);
//                    address.setText(getResources().getString(R.string.special_quarantine_center));
                    et_address.setFocusableInTouchMode(true);
                    et_address.setFocusable(true);
                } else {
                    et_address.setEnabled(true);
                    et_address.setFocusableInTouchMode(false);
                    et_address.setFocusable(false);
                }
                d.dismiss();
            }
        });
        d.show();
    }

    private void showTimePicker(TextInputEditText et, TextInputEditText et_end) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
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

            lm = (LocationManager) getSystemService(LOCATION_SERVICE);

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

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


}
