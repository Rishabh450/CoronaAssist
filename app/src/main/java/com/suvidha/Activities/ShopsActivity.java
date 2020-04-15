package com.suvidha.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Adapters.ExpandableListAdapter;
import com.suvidha.Models.GetShopsModel;
import com.suvidha.Models.ShopModel;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    private ExpandableListView expandableListView;

    String[] parent = new String[]{"What is View?", "What is  Layout?", "What is Dynamic Views?"};
    String[] q1 = new String[]{"List View", "Grid View"};
    String[] q2 = new String[]{"Linear Layout", "Relative Layout"};
    String[] q3 = new String[]{"Recycle View"};
    String[] des1 = new String[]{"A layout that organizes its children into a single horizontal or vertical row. It creates a scrollbar if the length of the window exceeds the length of the screen."};
    String[] des2 = new String[]{"Enables you to specify the location of child objects relative to each other (child A to the left of child B) or to the parent (aligned to the top of the parent)."};
    String[] des3 = new String[]{"This list contains linear layout information"};
    String[] des4 = new String[]{"This list contains relative layout information,Displays a scrolling grid of columns and rows"};
    String[] des5 = new String[]{"Under the RecyclerView model, several different components work together to display your data. Some of these components can be used in their unmodified form; for example, your app is likely to use the RecyclerView class directly. In other cases, we provide an abstract class, and your app is expected to extend it; for example, every app that uses RecyclerView needs to define its own view holder, which it does by extending the abstract RecyclerView.ViewHolder class."};

    LinkedHashMap<String, String[]> thirdLevelq1 = new LinkedHashMap<>();
    LinkedHashMap<String, String[]> thirdLevelq2 = new LinkedHashMap<>();
    LinkedHashMap<String, String[]> thirdLevelq3 = new LinkedHashMap<>();
    /**
     * Second level array list
     */
    List<String[]> secondLevel = new ArrayList<>();
    /**
     * Inner level data
     */
    List<LinkedHashMap<String, String[]>> dat = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        init();
        currentType = getIntent().getIntExtra("type",1);
        title = getIntent().getStringExtra("type_name");
        setUpAdapter();
        intialiseRetrofit();
//        getData();
//        manageToolbar();
//        setuprec();
    }

    private void setUpAdapter() {
        secondLevel.add(q1);
        secondLevel.add(q2);
        secondLevel.add(q3);
        thirdLevelq1.put(q1[0], des1);
        thirdLevelq1.put(q1[1], des2);
        thirdLevelq2.put(q2[0], des3);
        thirdLevelq2.put(q2[1], des4);
        thirdLevelq3.put(q3[0], des5);

        dat.add(thirdLevelq1);
        dat.add(thirdLevelq2);
        dat.add(thirdLevelq3);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        //passing three level of information to constructor
        ExpandableListAdapter expandableListAdapterAdapter = new ExpandableListAdapter(this, parent, secondLevel, dat);
        expandableListView.setAdapter(expandableListAdapterAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });


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
