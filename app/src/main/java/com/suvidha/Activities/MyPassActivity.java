package com.suvidha.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Adapters.PassAdapter;
import com.suvidha.Models.Pass;
import com.suvidha.Models.UserPassesResult;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.getAccessToken;


public class MyPassActivity extends AppCompatActivity {

    //Field Variables
    private static final String TAG = "My pass";
    private SharedPrefManager sharedPrefManager;
    RecyclerView.LayoutManager layoutManager;
    PassAdapter passAdapter;
    List<Pass> passes = new ArrayList<>();

    //Views
    FloatingActionButton fbAddPass;
    RecyclerView rvPasses;
    Toolbar toolbar;

    // Retrofit
    ApiInterface apiInterface;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        if (!isFinishing())
            fetchData();
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypass);

        initiliseAllViews();
        intialiseRetrofit();
        setToolbar();



//        UtilityFunctions.clearLoginSession(MyPassActivity.this);
        fbAddPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPassActivity.this, AddPassActivity.class);
                startActivity(intent);
            }
        });

        setRecyclerView();
        fetchData();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Passes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void askPolice(Intent intent) {
        final LayoutInflater inflater = getLayoutInflater();

        final View alertLayout = inflater.inflate(R.layout.dialog_police_verification, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        TextInputEditText etPassword = alertLayout.findViewById(R.id.police_password);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                String pass = etPassword.getText().toString().trim();
                if (pass.length() != 0) {
                    if (pass.equalsIgnoreCase("123456")) {

                        startActivity(intent);
                    } else {
                        etPassword.setError("Wrong  Password");
                    }
                } else {
                    etPassword.setError("Password field cannot be empty");
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();
    }


    private void fetchData() {
        Call<UserPassesResult> getPassesCall = apiInterface.getPasses(getAccessToken(this));
        Log.d(TAG, "fetchData: accesstoken" + (getAccessToken(this)));
        getPassesCall.enqueue(new Callback<UserPassesResult>() {
            @Override
            public void onResponse(Call<UserPassesResult> call, Response<UserPassesResult> response) {
                passes.clear();
                UserPassesResult userPassesResult = response.body();
                if (userPassesResult != null) {
                    List<Pass> p = userPassesResult.getPasses();
                    passes.addAll(p);
                }
//                Log.d("main", sharedPrefManager.getString(SharedPrefManager.Key.USER_ID)+ "onResponse: "+sharedPrefManager.getString(SharedPrefManager.Key.TOKEN) + response.message()+response.code());

                passAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<UserPassesResult> call, Throwable t) {
                Log.d("hello", "onFailure: " + t.getMessage());
                Toast.makeText(MyPassActivity.this, "Failed to fetch passes" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvPasses.setLayoutManager(layoutManager);
        passAdapter = new PassAdapter(this, passes, false);
        rvPasses.hasFixedSize();
        rvPasses.setAdapter(passAdapter);
//        rvPasses.addItemDecoration(new DividerItemDecoration(rvNotes.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void initiliseAllViews() {
        sharedPrefManager = SharedPrefManager.getInstance(this);
        fbAddPass = findViewById(R.id.main_add_pass);
        rvPasses = findViewById(R.id.mian_pass_recycler);
        toolbar = findViewById(R.id.main_toolbar);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

}