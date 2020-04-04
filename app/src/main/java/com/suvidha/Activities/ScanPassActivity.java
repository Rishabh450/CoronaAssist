package com.suvidha.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.suvidha.Models.Pass;
import com.suvidha.Models.RegistrationResult;
import com.suvidha.Models.SinglePassResult;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.Utils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanPassActivity extends AppCompatActivity {

    //Field variables

    private static final String TAG = "scan";

    // Views
    IntentIntegrator integrator;
    FloatingActionButton fabScan;
    TextView tvpassID, tvPassType, tvDuration, tvDate, tvReason, tvVehicleNum, tvStatusText, tvDestination, tvIdentity, tvPassanger, tvSenior, tvName;
    ImageView ivQR, ivStatusImage;
    androidx.core.widget.NestedScrollView nestedScroller;
    Toolbar toolbar;
    LinearLayout llApprove, llReject, llStatus;
    RelativeLayout rlChangeStatus;
    ProgressDialog progressDialog;

    //Retrofit
    ApiInterface apiInterface;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pass);

        Gson gson = new Gson();
        String passObject = getIntent().getStringExtra("pass");
        Pass passObj = gson.fromJson(passObject, Pass.class);
        boolean police = getIntent().getBooleanExtra("police", false);

        initialiseAllViews();
        intialiseRetrofit();
        Log.d(TAG, "onCreate: "+police+passObj);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PassDetails");

        if(passObj!=null){
            if(police){
                fabScan.setVisibility(View.VISIBLE);
                llStatus.setVisibility(View.GONE);
                rlChangeStatus.setVisibility(View.VISIBLE);
                llApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeStatus("1", passObj, passObj.getStatus());
                    }
                });

                llReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeStatus("-1", passObj ,passObj.getStatus());
                    }
                });

            }else{
                fabScan.setVisibility(View.GONE);
                llStatus.setVisibility(View.VISIBLE);
                rlChangeStatus.setVisibility(View.GONE);
            }
            setInitialValuesToViews(passObj);
        }else{
            if(police){
                fabScan.setVisibility(View.VISIBLE);
                llStatus.setVisibility(View.VISIBLE);
                rlChangeStatus.setVisibility(View.GONE);

            }else{
                fabScan.setVisibility(View.GONE);
                llStatus.setVisibility(View.VISIBLE);
                rlChangeStatus.setVisibility(View.GONE);
            }
        }

//        fabScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openscanner();
//            }
//        });



    }

    private void changeStatus(String i, Pass passObj, int status) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pid", passObj.getPassid());
        requestBody.put("status", i);
        Call<RegistrationResult> updatepassCall = apiInterface.updateStatus(requestBody);
        updatepassCall.enqueue(new Callback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                RegistrationResult result = response.body();
                if(result == null){
                    Toast.makeText(ScanPassActivity.this, "not recieved", Toast.LENGTH_SHORT).show();
//                    finish();
                }else{
                    Toast.makeText(ScanPassActivity.this, ""+result.getId(), Toast.LENGTH_SHORT).show();
                    finish();
                    finish();
                    Intent intent = new Intent(ScanPassActivity.this, AllPassActivity.class);
                    intent.putExtra("status", status);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Throwable t) {

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

    private void setInitialValuesToViews(Pass passObj) {
        tvpassID.setText(passObj.getUrgencyText());
        String typeText = "";
        switch (passObj.getType()) {
            case 0:
                typeText = "in-city";
                break;
            case 1:
                typeText = "in-state";
                break;
            case 2:
                typeText = "out-state";
                break;
        }
        tvPassType.setText(typeText);
        llStatus.setVisibility(View.VISIBLE);
        tvDestination.setText(passObj.getDestination());
        ivQR.setImageBitmap(Utils.getQRCode(passObj.getPassid()));
        tvDuration.setText(passObj.getTime() + "(Valid for:" + passObj.getDuration() + ")");
        tvDate.setText(passObj.getDate());
        tvIdentity.setText(passObj.getProof());
        tvReason.setText(passObj.getPurpose());
        tvVehicleNum.setText(passObj.getVehicle());
        String senior = passObj.isSeniorCitizen() ? "Yes" : "No";
        tvSenior.setText(senior);
        tvName.setText(passObj.getName());
        tvPassanger.setText(passObj.getPassengerCount() + "");

        switch (passObj.getStatus()) {
            case 0:
                ivStatusImage.setImageResource(R.drawable.ic_time);
                tvStatusText.setText("Pending, wait for you approval");
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.yellow_two));
                nestedScroller.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_one));
                break;
            case 1:
                ivStatusImage.setImageResource(R.drawable.ic_tick);
                tvStatusText.setText("Approved, by the authorising agency");
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.green));
                nestedScroller.setBackgroundColor(ContextCompat.getColor(this, R.color.qr_background));
                break;
            case -1:
                ivStatusImage.setImageResource(R.drawable.ic_criss_cross);
                tvStatusText.setText("Rejected, for the following reason");
                tvStatusText.setTextColor(ContextCompat.getColor(this, R.color.rejected_red_text));
                nestedScroller.setBackgroundColor(ContextCompat.getColor(this, R.color.rejected_red));
                break;

        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(ScanPassActivity.this, "No Scanned item found", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(ScanPassActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                consumeQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void consumeQRCode(String contents) {
        showMessage("Loading", "Please wait while we fetch the qr code");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pid",contents);
        rlChangeStatus.setVisibility(View.VISIBLE);
        Log.d(TAG, "consumeQRCode: "+requestBody);
//
        Call<SinglePassResult> getPassCall = apiInterface.getPass(requestBody);
        getPassCall.enqueue(new Callback<SinglePassResult>() {
            @Override
            public void onResponse(Call<SinglePassResult> call, Response<SinglePassResult> response) {
                Log.d(TAG, "onResponse: "+response.body());
                SinglePassResult passResult = response.body();
                Log.d(TAG, "onResponse: " + passResult);
                Pass pass = passResult.getPass();
                Log.d(TAG, "onResponse: "+pass);
                setInitialValuesToViews(pass);
                llApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeStatus("1", pass, pass.getStatus());
                        hideMessage();
                    }
                });

                llReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeStatus("-1", pass ,pass.getStatus());
                        hideMessage();
                    }
                });
            }

            @Override
            public void onFailure(Call<SinglePassResult> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                hideMessage();
            }
        });
    }

    private void initialiseAllViews() {

        fabScan = findViewById(R.id.scan_pass_scan);
        tvpassID = findViewById(R.id.scan_pass_passid);
        tvPassType = findViewById(R.id.scan_pass_type);
        tvDestination = findViewById(R.id.scan_pass_destination);
        ivQR = findViewById(R.id.scan_pass_qr);
        tvDuration = findViewById(R.id.scan_pass_duration);
        tvDate = findViewById(R.id.scan_pass_date);
        tvReason = findViewById(R.id.scan_pass_reason);
        tvVehicleNum = findViewById(R.id.scan_pass_vehicle_number);
        ivStatusImage = findViewById(R.id.scan_pass_approved_status_image);
        tvStatusText = findViewById(R.id.scan_approved_status_text);
        nestedScroller = findViewById(R.id.scan_nested_root);
        toolbar = findViewById(R.id.scan_pass_toolbar);
        tvIdentity = findViewById(R.id.scan_pass_identity);
        tvName = findViewById(R.id.scan_pass_name);
        tvPassanger = findViewById(R.id.scan_pass_passanger);
        tvSenior = findViewById(R.id.scan_pass_senior);
        rlChangeStatus = findViewById(R.id.scan_police_status_change_layout);
        llApprove = findViewById(R.id.police_status_change_approve_layout);
        llReject = findViewById(R.id.police_status_change_reject_layout);
        llStatus = findViewById(R.id.scan_pass_approved_status_layout);
        progressDialog = new ProgressDialog(this);
    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }


}
