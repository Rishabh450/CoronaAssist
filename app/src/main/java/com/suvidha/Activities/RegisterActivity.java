package com.suvidha.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Models.AddressModel;
import com.suvidha.Models.RegistrationResult;
import com.suvidha.Models.SMSverifcation;
import com.suvidha.Models.UserModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SMSClient;
import com.suvidha.Utilities.SMSInterface;
import com.suvidha.Utilities.SharedPrefManager;
import com.suvidha.Utilities.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.address;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.mStateDist;
import static com.suvidha.Utilities.Utils.setLoginSession;

public class RegisterActivity extends AppCompatActivity {


    //Field varibales
    private static final int READ_PHONE_STATE = 201;
    private static final String API_KEY = "37538f7f-7dc7-11ea-9fa5-0200cd936042";
    private final String TAG = "register";
    Dialog d;
    String sessionId = "";
    ProgressDialog progressDialog;


    //Retrofit
    ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    //Views
    private TextInputEditText etName, etPhone, etEmail, etAddress;
    private Button verifyPhone;
    private Spinner spinner_zone, spinner_state, spinner_district;
    private Button btnRegister;
    private boolean isVerified = true;
    private UserModel userData;
    private String mSelectedState, mSelectedDistrict;
    private List<String> mDistricts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Restore instance state

        userData = getIntent().getParcelableExtra("user_data");
        setSpinnerData();
        intialiseAllViews();
        intialiseRetrofit();
        if (sharedPrefManager.containsKey(SharedPrefManager.Key.USER_PHONE)) {
            etPhone.setEnabled(false);
            btnRegister.setEnabled(true);
            verifyPhone.setEnabled(false);
            etPhone.setText(sharedPrefManager.getString(SharedPrefManager.Key.USER_PHONE));
        } else {
            etPhone.setEnabled(true);
            btnRegister.setEnabled(false);
            verifyPhone.setEnabled(true);
        }
        mDistricts = new ArrayList<>();

//        Utils.parseJson(this);
        setSpinner();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        verifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString().trim();
                if (phone.length() != 0 && phone.length() == 10) {
                    showMessage("Sending", "Wait while we send you the otp");
                    sendOTP(phone);
                    createOTPDialog(phone);
                } else {
                    etPhone.setError("Please provide correct phone number");
                }
            }
        });


    }

    private void showMessage(String titile, String Message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                progressDialog.setTitle(titile);
                progressDialog.setMessage(Message);
                progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progressDialog.show();
            }
        });

    }

    private void hideMessage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread from Barcode Fragment");
                progressDialog.hide();

            }
        });
        return;
    }

    private void sendOTP(String phone) {
        SMSInterface apiService =
                SMSClient.getClient().create(SMSInterface.class);

        Call<SMSverifcation> call = apiService.sentOTP(API_KEY, phone);
        call.enqueue(new Callback<SMSverifcation>() {
            @Override
            public void onResponse(Call<SMSverifcation> call, Response<SMSverifcation> response) {
                sessionId = response.body().getDetails();
                Log.d("SenderID", sessionId);
                hideMessage();
                //you may add code to automatically fetch OTP from messages.
            }
            @Override
            public void onFailure(Call<SMSverifcation> call, Throwable t) {
                Log.e("ERROR", t.toString());
                hideMessage();
            }
        });
    }

    private void createOTPDialog(String phone) {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.dialog_phone_otp, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_otp);
        TextInputEditText etOTP = dialog.findViewById(R.id.register_otp);

        dialog.show();
        dialog.findViewById(R.id.dialog_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = etOTP.getText().toString().trim();
                if (pass.length() != 0) {
                    SMSInterface apiService =
                            SMSClient.getClient().create(SMSInterface.class);

                    Call<SMSverifcation> call = apiService.verifyOTP(API_KEY, sessionId, pass);

                    call.enqueue(new Callback<SMSverifcation>() {
                        @Override
                        public void onResponse(Call<SMSverifcation> call, Response<SMSverifcation> response) {

                            try {
                                if (response.body().getStatus().equals("Success")) {
                                    dialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                                    btnRegister.setEnabled(true);
                                    verifyPhone.setEnabled(false);
                                    etPhone.setEnabled(false);
                                    sharedPrefManager.put(SharedPrefManager.Key.USER_PHONE, phone);
                                } else {
                                    etOTP.setText("");
                                    etOTP.setError("Please enter correct OTP");
                                    Toast.makeText(RegisterActivity.this, "Please Enter correct OTP", Toast.LENGTH_SHORT).show();
                                    Log.d("Failure", response.body().getDetails() + "|||" + response.body().getStatus());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                hideMessage();
                            }
                        }

                        @Override
                        public void onFailure(Call<SMSverifcation> call, Throwable t) {
                            Log.e("ERROR", t.toString());
                            Toast.makeText(RegisterActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            hideMessage();
                        }

                    });
                } else {
                    etOTP.setError("OTP cannot be empty");
                }
            }
        });
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




    }

    private void setSpinnerData() {
        for (AddressModel model : address) {
            List<String> district = mStateDist.get(model.state);
            if (district == null) {
                district = new ArrayList<>();
            }
            district.add(model.district);
            mStateDist.put(model.state, district);
        }
    }

    private void setSpinner() {
        Object[] states = Utils.mStateDist.keySet().toArray();
        Arrays.sort(states);
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(aa1);
        mSelectedState = spinner_state.getSelectedItem().toString();
        mDistricts = Utils.mStateDist.get(mSelectedState);
        updateDistrictSpinner();
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedState = states[position].toString();
                updateDistrictSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateDistrictSpinner() {
        mDistricts = Utils.mStateDist.get(mSelectedState);
        ArrayAdapter aa2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mDistricts);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_district.setAdapter(aa2);
        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedDistrict = spinner_district.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPrefManager.containsKey(SharedPrefManager.Key.LOGIN_STATUS)) {
            boolean loginStatus = sharedPrefManager.getBoolean(SharedPrefManager.Key.LOGIN_STATUS);
            if (loginStatus) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }


    private void register() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        mSelectedState = spinner_state.getSelectedItem().toString().trim();
        mSelectedDistrict = spinner_district.getSelectedItem().toString().trim();

        if (name.length() != 0 && phone.length() != 0 && phone.length() == 10 && isVerified && etEmail.length() != 0 && address.length() != 0 && mSelectedState.length() != 0 && mSelectedDistrict.length() != 0) {
//            List<String> passes = new ArrayList<>();
            final UserModel user = new UserModel(name, email, phone, address, mSelectedState, mSelectedDistrict);
//            Log.e(TAG,getAccessToken(RegisterActivity.this));
            Call<RegistrationResult> registerCall = apiInterface.register(getAccessToken(RegisterActivity.this), user);
            registerCall.enqueue(new Callback<RegistrationResult>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
//                    Toast.makeText(RegisterActivity.this, "Registration Successful." + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse: " + response.body().getId());
                    if (response.body().getStatus() == 201) {
                        setLoginSession(user, RegisterActivity.this);
                        sharedPrefManager.put(SharedPrefManager.Key.USER_ID, response.body().getId());
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration not successful. Try again later", Toast.LENGTH_SHORT).show();
                    }
//                    finishAffinity();
                }

                @Override
                public void onFailure(Call<RegistrationResult> call, Throwable t) {

                }
            });

        } else {
            if (name.length() == 0) {
                etName.setError("Name cannot be empty");
            }
            if (phone.length() == 0) {
                etPhone.setError("Phone number cannot be empty");
            }
            if (address.length() == 0) {
                etAddress.setError("address cannot be empty");
            }
            if (phone.length() != 10) {
                etPhone.setError("Invalid phone number");
            }
        }
    }

    private void intialiseAllViews() {
        sharedPrefManager = SharedPrefManager.getInstance(this);
        etName = findViewById(R.id.register_name);
        etName.setText(userData.getName());
        etPhone = findViewById(R.id.register_mobile);
        btnRegister = findViewById(R.id.register_submit);
        etEmail = findViewById(R.id.register_email);
        etEmail.setText(userData.email);
        etAddress = findViewById(R.id.register_address);
        spinner_zone = findViewById(R.id.register_zone);
        verifyPhone = findViewById(R.id.register_verify_btn);
        spinner_state = findViewById(R.id.register_state);
        spinner_district = findViewById(R.id.register_district);
        progressDialog = new ProgressDialog(this);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

}
