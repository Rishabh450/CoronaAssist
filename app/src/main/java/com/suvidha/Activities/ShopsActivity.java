package com.suvidha.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Models.GetShopsModel;
import com.suvidha.Models.ShopModel;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.suvidha.Utilities.Utils.currentType;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.local_zone_name;

public class ShopsActivity extends AppCompatActivity {
    private static final String TAG = "ShopsActivity";
    private RecyclerView rView;
    private Toolbar toolbar;
    ApiInterface apiInterface;
    private ShopListAdapter mAdapter;
    private String title;
    private List<ShopModel> data=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        init();
        currentType = getIntent().getIntExtra("type",1);
        title = getIntent().getStringExtra("type_name");
        intialiseRetrofit();
        getData();
        manageToolbar();
        setuprec();
    }

    private void getData() {
        GetShopsModel model = new GetShopsModel(currentType, local_zone_name);
//        Log.e(TAG, "ACCESSTOKEN: "+getAccessToken(this));
        Call<ShopRequestModel> listCallResult = apiInterface.getAllShops(getAccessToken(this), model);
        listCallResult.enqueue(new Callback<ShopRequestModel>() {
            @Override
            public void onResponse(Call<ShopRequestModel> call, Response<ShopRequestModel> response) {
//                if (response.body().id != null)
//                    Log.e(TAG, "onResponse: " + response.body().id.get(0).name);
                data.clear();
                data.addAll(response.body().id);
                Log.e("SHOP ID","+"+data.get(0)._id);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ShopRequestModel> call, Throwable t) {
                Log.e(TAG, "onResponseError" + t.getMessage());
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        rView = findViewById(R.id.groc_cat_rview);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
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
