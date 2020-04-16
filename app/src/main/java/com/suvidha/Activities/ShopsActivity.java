package com.suvidha.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.suvidha.Adapters.CustomExpandableListAdapter;
import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Models.GetShopsModel;
import com.suvidha.Models.ShopModel;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.clearLoginSession;
import static com.suvidha.Utilities.Utils.currentType;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.local_zone_name;
import static com.suvidha.Utilities.Utils.order_address;

public class ShopsActivity extends AppCompatActivity {
    private static final String TAG = "ShopsActivity";
    private RecyclerView rView;
    private Toolbar toolbar;
    ApiInterface apiInterface;
    private ShopListAdapter mAdapter;
    private String title;
    private List<ShopModel> data=new ArrayList<>();
    private RelativeLayout no_shops;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        init();
        intialiseRetrofit();
        getData();
        manageToolbar();
        setuprec();
    }


    private void getData() {

//        Log.e(TAG, "ACCESSTOKEN: "+getAccessToken(this));
        Call<ShopRequestModel> listCallResult = apiInterface.getAllShops(getAccessToken(this));
        listCallResult.enqueue(new Callback<ShopRequestModel>() {
            @Override
            public void onResponse(Call<ShopRequestModel> call, Response<ShopRequestModel> response) {
//                if (response.body().id != null)
//                    Log.e(TAG, "onResponse: " + response.body().id.get(0).name);
                if(response.body().status !=302) {
                    if (response.body().status == 200) {
                        data.clear();
                        data.addAll(response.body().id);
                        Log.e("SHOP ID", "+" + data.get(0)._id);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShopsActivity.this, "No shops exists", Toast.LENGTH_SHORT).show();
                    }
                }else{
                   signOut();
                }
            }

            @Override
            public void onFailure(Call<ShopRequestModel> call, Throwable t) {
                Log.e(TAG, "onResponseError" + t.getMessage());
                no_shops.setVisibility(View.VISIBLE);
            }
        });
    }
    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(ShopsActivity.this, gso);
        googleSignInClient.signOut().addOnCompleteListener(ShopsActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //clear data
                clearLoginSession(ShopsActivity.this);

            }
        });
    }
    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        rView = findViewById(R.id.groc_cat_rview);
        no_shops = findViewById(R.id.no_shops);
        order_address = SharedPrefManager.getInstance(this).getString(SharedPrefManager.Key.USER_ADDRESS);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.shop));
    }

    void setuprec() {
        rView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShopListAdapter(this, data);
        rView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }




}
