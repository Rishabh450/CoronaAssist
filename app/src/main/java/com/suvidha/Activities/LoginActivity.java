package com.suvidha.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.suvidha.Models.LoginResult;
import com.suvidha.Models.UserModel;
import com.suvidha.Models.ZonesModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.ServiceGenerator;
import com.suvidha.Utilities.SharedPrefManager;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.CAMERA_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.LOCATION_PERMISSION_CODE;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.isLoggedIn;
import static com.suvidha.Utilities.Utils.password;
import static com.suvidha.Utilities.Utils.setLoginSession;
import static com.suvidha.Utilities.Utils.zonesList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    ApiInterface apiInterface;
    Dialog dialog;
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv = findViewById(R.id.logotext);

        Shader textShader=new LinearGradient(0,0, 300, 20,
                new int[]{Color.parseColor("#F0931F"),Color.parseColor("#0B9243")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        tv.getPaint().setShader(textShader);
        intialiseRetrofit();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void updateUI(GoogleSignInAccount account) {
        if (account != null) {
//            getPermissions();
            if (isLoggedIn(LoginActivity.this)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, account.getDisplayName());
                Log.e(TAG, account.getEmail());
                UserModel user = new UserModel(account.getDisplayName(), account.getEmail());
                ApiInterface loginService =
                        ServiceGenerator.createService(ApiInterface.class, user.email, password);
                Call<LoginResult> loginResultCall = loginService.login(user);
                loginResultCall.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        try {
                            Log.d(TAG, "onResponse: " + response.body().id.token);
                            //store access token
                            Intent intent = null;
                            zonesList.clear();
                            zonesList.addAll(response.body().zone);
                            if(response.body().location != null){
                                SharedPrefManager.getInstance(LoginActivity.this).
                                        put(SharedPrefManager.Key.QUARENTINE_LAT_KEY,response.body().location.location_lat);
                                SharedPrefManager.getInstance(LoginActivity.this).
                                        put(SharedPrefManager.Key.QUARENTINE_LON_KEY,response.body().location.location_lon);
                            }
                            SharedPrefManager.getInstance(LoginActivity.this).storeToken(response.body().id.token);
                            if (response.body().status == 205 || response.body().id.phone == null) {
                                // goto register activity
                                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                intent.putExtra("user_data", response.body().id);
                                startActivity(intent);
                                finish();

                            } else {
                                //go to main activity
                                Log.e("TAG", String.valueOf(getZoneId(response.body().id,response.body().zone)));
                                setLoginSession(response.body().id, LoginActivity.this,getZoneId(response.body().id,response.body().zone));
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Log.d(TAG, "onResponseError: " + t.getMessage());
                    }
                });
//            Intent intent = new Intent(this,MainActivity.class);
//            startActivity(intent);
//            finish();
            }
        }
    }

    private void getPermissions() {
        if(checkCameraPermission()){
            requestCameraPermission();
        }
        if(checkLocationPermissions()){
            requestLocationPermissions();
        }
    }
    private boolean checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    private boolean checkCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );
    }
    private int getZoneId(UserModel user, List<ZonesModel> zone) {
        for(int i=0;i<zone.size();i++){
            if(user.zone.compareTo(zone.get(i).name)==0){
                return zone.get(i).id;
            }
        }
        return 0;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                dialog = createProgressDialog(this,"Please Wait");
                signIn();
                break;
            // ...
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                Log.e(TAG,"CAMERA GRANTED");
            }
        }else if(requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                Log.e(TAG,"LOCATION GRANTED");
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.dismiss();
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else {

        }
    }

}
