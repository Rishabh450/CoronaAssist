package com.suvidha.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Models.ShopModel;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import static com.suvidha.Utilities.Utils.clearLoginSession;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.is_delivery;
import static com.suvidha.Utilities.Utils.order_address;


public class Grocery extends Fragment {
    private static final String TAG = "ShopsActivity";
    private RecyclerView rView;
    private Toolbar toolbar;
    ApiInterface apiInterface;
    private ShopListAdapter mAdapter;
    private String title;
    private List<ShopModel> data=new ArrayList<>();
    private RelativeLayout no_shops;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_grocery, container, false);
        init(v);
        intialiseRetrofit();
        getData();

        setuprec();
        return v;
    }

    private void getData() {
        Log.d("categoryy","get");

//        Log.e(TAG, "ACCESSTOKEN: "+getAccessToken(this));
        Call<ShopRequestModel> listCallResult = apiInterface.getAllShops(getAccessToken(getContext()));
        listCallResult.enqueue(new Callback<ShopRequestModel>() {
            @Override
            public void onResponse(Call<ShopRequestModel> call, Response<ShopRequestModel> response) {
//                if (response.body().id != null)
                Log.d("categoryy","ret");
//                    Log.e(TAG, "onResponse: " + response.body().id.get(0).name);
                if(response.body().status !=302) {
                    if (response.body().status == 200) {
                        data.clear();


                        List<ShopModel> temp=new ArrayList<>();


                        temp.addAll(response.body().id);
                        Log.d("categoryy",temp.size()+" ");
                        for(int i=0;i<temp.size();i++)
                        {
                            Log.d("categoryy",temp.size()+" "+temp.get(i).type);
                            if(!temp.get(i).type.equals("Medicines"))
                                data.add(temp.get(i));
                            Log.d("categoryy",temp.size()+" "+temp.get(i).address);
                        }

                       // Log.e("SHOP ID", "+" + data.get(0)._id);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No shops exists", Toast.LENGTH_SHORT).show();
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
    private void init(View view) {
        toolbar =view. findViewById(R.id.default_toolbar);
        rView = view. findViewById(R.id.groc_cat_rview);
        no_shops =view. findViewById(R.id.no_shops);
        order_address = SharedPrefManager.getInstance(getContext()).getString(SharedPrefManager.Key.USER_ADDRESS);
    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //clear data
                clearLoginSession(getContext());

            }
        });
    }


    void setuprec() {
        rView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ShopListAdapter(getContext(), data);
        rView.setAdapter(mAdapter);
    }
}
