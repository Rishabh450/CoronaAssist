package com.suvidha.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Models.AddressModel;
import com.suvidha.Models.RegistrationResult;
import com.suvidha.Models.UserModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;
import com.suvidha.Utilities.Utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.address;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.mStateDist;
import static com.suvidha.Utilities.Utils.setLoginSession;
import static com.suvidha.Utilities.Utils.zonesList;

public class RegisterActivity extends AppCompatActivity {


    //Field varibales
    private static final int READ_PHONE_STATE = 201;
    private final String TAG = "register";
    private SharedPrefManager sharedPrefManager;

    //Views
    private TextInputEditText etName, etPhone, etEmail, etAddress;
    private Button verifyPhone;
    private Spinner spinner_zone, spinner_state, spinner_district;
    private Button btnRegister;
    private boolean isVerified = true;
    //Retrofit
    ApiInterface apiInterface;
    private UserModel userData;
    private String mSelectedState, mSelectedDistrict;
    private List<String> mDistricts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userData = getIntent().getParcelableExtra("user_data");
        setSpinnerData();
        intialiseAllViews();
        intialiseRetrofit();
        mDistricts = new ArrayList<>();

//        Utils.parseJson(this);
        setSpinner();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void setSpinnerData() {
        for(AddressModel model:address){
            List<String> district = mStateDist.get(model.state);
            if(district == null){
                district = new ArrayList<>();
            }
            district.add(model.district);
            mStateDist.put(model.state,district);
        }
    }

    private void setSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getList());
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_zone.setAdapter(aa);
        Object states[] = Utils.mStateDist.keySet().toArray();
        Arrays.sort(states);
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(aa1);
        mSelectedState = spinner_state.getSelectedItem().toString();
        mDistricts = Utils.mStateDist.get(mSelectedState);

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
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < zonesList.size(); i++) {
            list.add(zonesList.get(i).name);
        }
        return list;
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
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

}
