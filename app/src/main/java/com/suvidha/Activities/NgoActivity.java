package com.suvidha.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suvidha.Adapters.NgoAdapter;
import com.suvidha.Adapters.QuarantineAdapter;
import com.suvidha.Models.FetchNgomodel;
import com.suvidha.Models.NgoModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.util.List;

import static com.suvidha.Utilities.Utils.getAccessToken;

public class NgoActivity extends AppCompatActivity {
    String ngoname;
    List<com.suvidha.Models.NgoActivity> list;
    ApiInterface apiInterface;
    RecyclerView recyclerView;
    NgoAdapter ngoAdapter;
    ProgressBar progressBar;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo);
        intialiseRetrofit();
        Intent intent=getIntent();
        ngoname=intent.getStringExtra("name").trim();
        name=findViewById(R.id.ngo_nm);
        name.setText(ngoname);
        Log.d("ngonam",ngoname+" ");
        getData();
        recyclerView=findViewById(R.id.ngoact);
        progressBar=findViewById(R.id.prog);

    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void setRecyclerView() {
        recyclerView .setLayoutManager(new LinearLayoutManager(this));
        ngoAdapter = new NgoAdapter(this,list);
        recyclerView.setAdapter(ngoAdapter);
        progressBar.setVisibility(View.GONE);

    }
    public void getData()
    {
        Call<FetchNgomodel> getReportsModelCall = apiInterface.get_ngo(getAccessToken(this));
        getReportsModelCall.enqueue(new Callback<FetchNgomodel>() {

            @Override
            public void onResponse(Call<FetchNgomodel > call, Response<FetchNgomodel> response) {
                List<NgoModel> data=  response.body().getId();
                Log.d("ngolisize",data.size()+" ");
                for(int l=0;l<data.size();l++)
                {

                    if(response.body().getId().get(l).getName().equals(ngoname))
                    {
                        Log.d("ngolis",response.body().getId().get(l).getName());
                        if(response.body().getId().get(l).getActivities()!=null)
                        {

                            list=response.body().getId().get(l).getActivities();
                            Log.d("ngolis","mil gya"+list.size());
                            setRecyclerView();
                            break;
                        }



                    }
                }





//                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<FetchNgomodel> call, Throwable t) {
                Log.d("failedhey", String.valueOf(t));

            }


        });

    }
}
