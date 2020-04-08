package com.suvidha.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Location related
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 555;
    int ACCESS_FINE_LOCATION_CODE = 3310;
    int ACCESS_COARSE_LOCATION_CODE = 3410;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;

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
        buildGoogleApiClient();
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

    // When user first come to this activity we try to connect Google services for location and map related work
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    // Google Api Client is connected
    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            //if connected successfully show user the settings dialog to enable location from settings services
            // If location services are enabled then get Location directly
            // Else show options for enable or disable location services
            settingsrequest();
        }
    }


    // This is the method that will be called if user has disabled the location services in the device settings
    // This will show a dialog asking user to enable location services or not
    // If user tap on "Yes" it will directly enable the services without taking user to the device settings
    // If user tap "No" it will just Finish the current Activity
    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (mGoogleApiClient.isConnected()) {

                            // check if the device has OS Marshmellow or greater than
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
                                } else {
                                    // get Location
                                }
                            } else {
                                // get Location
                            }

                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
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
                            if(response.body().location.location_lat != 0.0&&response.body().location.location_lon != 0.0){
                                Log.d("checkingloc", String.valueOf(response.body().location)+" ");
                                SharedPrefManager.getInstance(LoginActivity.this).put(SharedPrefManager.Key.IS_QUARANTINE,1);

                                SharedPrefManager.getInstance(LoginActivity.this).
                                        put(SharedPrefManager.Key.QUARENTINE_LAT_KEY,response.body().location.location_lat);
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
                Log.e(TAG, "CAMERA GRANTED");
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                Log.e(TAG, "LOCATION GRANTED");
            }
        } else if (requestCode == 3310) {

            if (grantResults.length > 0) {

                for (int i = 0, len = permissions.length; i < len; i++) {

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Please switch on your location", Toast.LENGTH_SHORT).show();
                    } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // get Location
                    } else {
                        this.finish();
                    }
                }
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
        } else if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // get location method
                    break;
                case Activity.RESULT_CANCELED:
                    this.finish();
                    break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    // When there is an error connecting Google Services
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    // Connect Google Api Client if it is not connected already
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // Stop the service when we are leaving this activity
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((LoginActivity) getActivity()).onDialogDismissed();
        }
    }

}
