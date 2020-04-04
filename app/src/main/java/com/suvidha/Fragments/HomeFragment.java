package com.suvidha.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Activities.MainActivity;
import com.suvidha.Activities.QuarantineActivity;
import com.suvidha.Activities.ShopsActivity;
import com.suvidha.Activities.MyPassActivity;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.QuarantineModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.LOCATION_LAT;
import static com.suvidha.Utilities.Utils.LOCATION_LON;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createProgressDialog;
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

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        intialiseRetrofit();
        setListeners();
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
                    getCurrentLocation();
                } else {
                    requestLocationPermissions();
                    Toast.makeText(getContext(), "You don't have location permission", Toast.LENGTH_SHORT).show();
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
                            //select the node
//                            Log.e(TAG, "Response Error " + .getMessage());

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
                            Toast.makeText(getContext(), "Can't get your location", Toast.LENGTH_SHORT).show();
                        } else {
                            quarantineLocation =new Location(location);
                            dialog.dismiss();
                            Log.e("LOL", String.valueOf(location.getLatitude()));
//                            LOCATION_LAT = quarantineLocation.getLatitude();
//                            LOCATION_LON = quarantineLocation.getLongitude();
                            if(is_quarantined==1){
                                Intent intent = new Intent(getContext(), QuarantineActivity.class);
                                intent.putExtra("lat",location.getLatitude());
                                intent.putExtra("lon",location.getLongitude());
                                startActivity(intent);
                            }else {
                                createQuarentineDialog();
                            }
                        }
                    }
                }
        );
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
        phone = dialog.findViewById(R.id.quarantine_mobile);
        address = dialog.findViewById(R.id.quarantine_address);
        tnc = dialog.findViewById(R.id.tnc);
        cancel = dialog.findViewById(R.id.quarantine_cancel);
        st = dialog.findViewById(R.id.quarantine_start_date);
        end = dialog.findViewById(R.id.quarantine_end_date);
        register = dialog.findViewById(R.id.quarantine_register);
        authority = dialog.findViewById(R.id.quarantine_authority);
        TextView termsandCondition = dialog.findViewById(R.id.terms_n_condition);
        termsandCondition.setText(Html.fromHtml(getContext().getResources().getString(R.string.terms_and_condition)));
        register.setEnabled(false);
        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(st);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(end);
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
                boolean terms = tnc.isChecked();
                if (etname.length() != 0 && etphone.length() != 0 && etphone.length() == 10 && etaddress.length() != 0 && stDate.length() != 0 && endDate.length() != 0 && etAuthority.length() != 0 && terms) {
//                    register quarantine
                    QuarantineModel model = new QuarantineModel(etname, etaddress, etphone, (float) quarantineLocation.getLatitude(), (float) quarantineLocation.getLongitude(), etAuthority, stDate, endDate);

                    Call<GeneralModel> registerResult = apiInterface.register_quarantine(getAccessToken(getContext()), model);
                    registerResult.enqueue(new Callback<GeneralModel>() {
                        @Override
                        public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                            if (response.body().status == 200) {
                                //success
                                is_quarantined = 1;
                                notifyDataLoaded();
                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.QUARENTINE_LAT_KEY, (float) quarantineLocation.getLatitude());
                                SharedPrefManager.getInstance(getContext()).put(SharedPrefManager.Key.QUARENTINE_LON_KEY, (float) quarantineLocation.getLongitude());
                                Toast.makeText(getContext(), "Successfully Registered for quarantine", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Failed to register for quarantine", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralModel> call, Throwable t) {
                            Toast.makeText(getContext(), "Failed to register for quarantine", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    if (etname.length() == 0) {
                        name.setError("Name Field cannot be empty");
                    }
                    if (etaddress.length() == 0) {
                        address.setError("Address Field cannot be empty");
                    }
                    if (etphone.length() != 10) {
                        phone.setError("Enter a valid phone number");
                    }
                    if (etAuthority.length() == 0) {
                        authority.setError("Authority Name cannot be empty");
                    }
                    if (stDate.length() == 0) {
                        st.setError("Pick the start date");
                    }
                    if (endDate.length() == 0) {
                        end.setError("Pick the end date");
                    }
                    if (!tnc.isChecked()) {
                        Toast.makeText(getContext(), "Please check the terms and condition", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showTimePicker(TextInputEditText et) {
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et.setText(dayOfMonth + "-" + month + "-" + year);
                    }
                }, 2020, 4, 4);
        picker.show();
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                (MainActivity) getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                createQuarentineDialog();
            }
        }
    }

    @Override
    public void notifyDataLoaded() {
        Log.e("LOL", String.valueOf(is_quarantined));
        if (is_quarantined == 1) {
            iconQuarentine.setText("Quarentine");
        } else {
            iconQuarentine.setText("Register Quarentine");
        }
    }


}
