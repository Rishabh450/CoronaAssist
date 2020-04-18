package com.suvidha.Activities;

import android.content.Intent;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.suvidha.Adapters.CustomExpandableListAdapter;
import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Fragments.Grocery;
import com.suvidha.Fragments.Pharma;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class ShopsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "ShopsActivity";
    private RecyclerView rView;
    private Toolbar toolbar;
    ApiInterface apiInterface;
    private ShopListAdapter mAdapter;
    private String title;
    private List<ShopModel> data=new ArrayList<>();
    private RelativeLayout no_shops;
    BottomNavigationView bott;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        init();
        manageToolbar();
        bott.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.grocery:

                        //do somthing
                        Grocery grocery=new Grocery();
                        fragmentTransaction.replace(R.id.fragment_container,grocery);
                        fragmentTransaction.commit();
                        break;
                    case R.id.medical:
                        Pharma pharma=new Pharma();
                        fragmentTransaction.replace(R.id.fragment_container,pharma);
                        fragmentTransaction.commit();
                        break;

                }
                return true ;
            }
        });
    }







/*    private void signOut() {
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
    }*/
    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        bott=findViewById(R.id.bott);

        order_address = SharedPrefManager.getInstance(this).getString(SharedPrefManager.Key.USER_ADDRESS);
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        Grocery grocery=new Grocery();
        fragmentTransaction.add(R.id.fragment_container,grocery);
        fragmentTransaction.commit();
    }



    void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.shop));
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
