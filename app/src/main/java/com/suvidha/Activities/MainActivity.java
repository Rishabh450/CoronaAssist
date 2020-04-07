package com.suvidha.Activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.suvidha.Fragments.HistoryFragment;
import com.suvidha.Fragments.HomeFragment;
import com.suvidha.Models.EssentialsRequestModel;
import com.suvidha.Models.GetOrdersModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.LiveLocationService;
import com.suvidha.Utilities.SharedPrefManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.PLAYSTORE_LINK;
import static com.suvidha.Utilities.Utils.allOrders;
import static com.suvidha.Utilities.Utils.clearLoginSession;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.local_zone_name;
import static com.suvidha.Utilities.Utils.zonesList;
import static com.suvidha.Utilities.Utils.is_quarantined;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private LinearLayout locationLayout;
    private TextView nodeName;

    private ApiInterface apiInterface;
    private int backFlag = 0;
    private BottomNavigationView navigation;
    private ViewPager mPager;
    MenuItem prevMenuItem;
    private ImageView signout;
    private String currentVersion;
    private Button btn;
    Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setBottomNavigation();
        intialiseRetrofit();
        setListeners();
        getEssentials();
        LiveLocationService mYourService = new LiveLocationService();
        mServiceIntent = new Intent(MainActivity.this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }


    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(f instanceof HistoryFragment){
            //go to home fragment
            navigation.setSelectedItemId(R.id.navigation_home);
            loadFragment(new HomeFragment());

        }else{
            Handler backHandler = new Handler();

            if (backFlag == 1) {
                finish();
            }
            backFlag = 1;
            Toast.makeText(MainActivity.this, R.string.backPress, 3000).show();
            backHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backFlag = 0;
                }
            }, 2500);
        }

    }
    private void getEssentials() {
        if (dialog == null) {
            dialog = createProgressDialog(this, "Please wait");
        }
        progressBar = dialog.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
        staticProgress.setVisibility(View.GONE);
        dialog.show();
        Call<EssentialsRequestModel> essentialsRequestModelCall = apiInterface.getEssentials(getAccessToken(this));
//        Log.e(TAG,"Response Error "+"LOL");
        essentialsRequestModelCall.enqueue(new Callback<EssentialsRequestModel>() {
            @Override
            public void onResponse(Call<EssentialsRequestModel> call, Response<EssentialsRequestModel> response) {

                try {
                    if (response.body().status == 200) {

                        dialog.dismiss();
                        onStart();
                        zonesList.clear();
                        zonesList.addAll(response.body().id.zones);

                        is_quarantined = response.body().id.is_quarantined;
                        Log.e(TAG, String.valueOf(is_quarantined));
                        if (getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HomeFragment) {
                            NotifyFragment callBack = (NotifyFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                            callBack.notifyDataLoaded();
                        }
                        //response.body().id.shop_types;
                        APP_CHARGE = response.body().id.cess_rate;
                        DELIVERY_CHARGE = response.body().id.delivery_cost;

                        local_zone_name = SharedPrefManager.getInstance(MainActivity.this).getInt(SharedPrefManager.Key.ZONE_KEY);
                        nodeName.setText(zonesList.get(local_zone_name).name);
                    } else {
                        TextView msg = dialog.findViewById(R.id.progress_msg);
                        msg.setText("Try Again");
                    }
                } catch (Exception e) {

                    TextView msg = dialog.findViewById(R.id.progress_msg);
                    msg.setText("Try Again");
                }

            }

            @Override
            public void onFailure(Call<EssentialsRequestModel> call, Throwable t) {
                Log.e(TAG, "Response Error " + t.getMessage());

                TextView msg = dialog.findViewById(R.id.progress_msg);
                msg.setText(getResources().getString(R.string.try_again));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar = dialog.findViewById(R.id.progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);
                        ImageView staticProgress = dialog.findViewById(R.id.static_progress);
                        staticProgress.setVisibility(View.VISIBLE);
                        staticProgress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getEssentials();
                            }
                        });
                    }
                }, 500);

                Toast.makeText(MainActivity.this, getResources().getString(R.string.failed_to_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllOrders() {
        Call<GetOrdersModel> listCall = apiInterface.getAllOrders(getAccessToken(this));
        listCall.enqueue(new Callback<GetOrdersModel>() {
            @Override
            public void onResponse(Call<GetOrdersModel> call, Response<GetOrdersModel> response) {
                if (response.body().status == 200) {

                    allOrders.clear();
                    allOrders.addAll(response.body().id);
                    if (getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HistoryFragment) {
                        NotifyFragment callBack = (NotifyFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        callBack.notifyDataLoaded();
                    }
//                    Collections.sort(allOrders,Collections.reverseOrder());
                } else {
//                    Toast.makeText(MainActivity.this, "Failed to get your history", Toast.LENGTH_SHORT).show();
                    Log.e("LOL", "Failed to get your history");
                }
            }

            @Override
            public void onFailure(Call<GetOrdersModel> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to connect to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetCurrentVersion extends AsyncTask<Void, Void, Void> {

        private String latestVersion;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect(PLAYSTORE_LINK)
                        .timeout(30000)
//                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                latestVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!TextUtils.isEmpty(currentVersion) && !TextUtils.isEmpty(latestVersion)) {
//                Log.d("hello", doc.toString());
//                Log.d("hello", "Current : " + currentVersion + " Latest : " + latestVersion);
                if (currentVersion.compareTo(latestVersion) < 0) {
                    if (!isFinishing()) {
                        showUpdateDialog();
                    }
                }
            }
            super.onPostExecute(aVoid);
        }
    }
    private void compareAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            currentVersion = pInfo.versionName;
            new GetCurrentVersion().execute();
        } catch (PackageManager.NameNotFoundException e1) {
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void showUpdateDialog() {
        Dialog dialog = createAlertDialog(this,"Update Required","A newer version of apk is available at playstore. Please Update","Cancel","Update");
        dialog.setCancelable(true);
        dialog.show();
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ojass20.nitjsr.in.ojass&hl=en")));
                dialog.dismiss();
                dialog.dismiss();
                finish();
            }
        });
    }

    public Dialog dialog;
    public ProgressBar progressBar;


        private void intialiseRetrofit() {
            apiInterface = APIClient.getApiClient().create(ApiInterface.class);
        }

    private void setBottomNavigation() {
        loadFragment(new HomeFragment());
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_history:
                    fragment = new HistoryFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setListeners() {
        locationLayout.setOnClickListener(this);
        signout.setOnClickListener(this);
    }

    private void init() {
        nodeName = findViewById(R.id.node_name);
        locationLayout = findViewById(R.id.node_location_layout);
        signout = findViewById(R.id.sign_out);
        btn = findViewById(R.id.change_to_hindi);
        Button eng = findViewById(R.id.change_to_english);

        findViewById(R.id.change_to_english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eng.setEnabled(false);
                btn.setEnabled(true);
                String languageToLoad  = "en";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config,getResources().getDisplayMetrics());

                recreate();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String languageToLoad  = "hi";
                btn.setEnabled(false);
                eng.setEnabled(true);
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config,getResources().getDisplayMetrics());

                recreate();
            }
        });
    }
    //suvidhajamshedhpur@gmail.com

    private void startDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_zone);
        setRecyclerViewInDialog(dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330/*height value*/, getResources().getDisplayMetrics()); for custom height value
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    void setRecyclerViewInDialog(Dialog dialog) {
        ListView lview = dialog.findViewById(R.id.dialog_zone_lview);
        List<String> items = new ArrayList<>();
        for (int i = 1; i < zonesList.size(); i++) {
            items.add(zonesList.get(i).name);
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lview.setAdapter(itemsAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                nodeName.setText(tv.getText().toString());
                local_zone_name = position + 1;
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.node_location_layout:
                startDialog();
                break;
            case R.id.sign_out:{
                Dialog dialog = createAlertDialog(this,"Sign Out","Are you sure you want to sign out","Cancel","Ok");
                dialog.setCancelable(false);
                dialog.show();
                dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                        dialog.dismiss();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        finish();
                    }
                });

            }
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //clear data
                clearLoginSession(MainActivity.this);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllOrders();
        compareAppVersion();
    }

    public interface NotifyFragment {
        void notifyDataLoaded();
        Location l = new Location("");


    }
}


//

//
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//                LocationManager.NETWORK_PROVIDER
//        );
//    }

//    private void getNearestNode() {
//
//        // Execute some code after 1 second has passed
//        nodeProgress.setVisibility(View.VISIBLE);
//        nodeName.setText("");
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                if (isNetworkConnected() && checkPermissions())
//                    new LocationStuff().execute();
//                else {
//                    handler.postDelayed(this, 1000);
//                }
//            }
//        }, 1000);
//
//    }

//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
//    }


