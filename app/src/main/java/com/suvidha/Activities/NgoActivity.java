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
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.suvidha.Utilities.Utils.getAccessToken;

public class NgoActivity extends AppCompatActivity {
    String ngoname;
    List<com.suvidha.Models.NgoActivity> list=new ArrayList<>();
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
                            List <com.suvidha.Models.NgoActivity> lister=response.body().getId().get(l).getActivities();
                            com.suvidha.Models.NgoActivity ngo=lister.get(0);
                            int i=0;
                           if(ngo!=null) {
                            for(;i<lister.size();i++) {
                                ngo=lister.get(i);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm");
                                Date strDate = null;
                                try {
                                    strDate = sdf.parse(ngo.getDatetime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (System.currentTimeMillis() > (sdf.parse(ngo.getDatetime())).getTime()) {
                                     //   list.add(ngo);

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(NgoActivity.this, "added" + e, Toast.LENGTH_LONG).show();

                                }
                                try{

                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");


                                    String str1 = ngo.getDatetime().substring(0,ngo.getDatetime().indexOf(' '));
                                    Date date1 = formatter.parse(str1);

                                    Date c = Calendar.getInstance().getTime();
                                    System.out.println("Current time => " + c);

                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                    String currentdate = df.format(c);
                                    Date date2 = formatter.parse(currentdate);

                                    if (date1.compareTo(date2)>0)
                                    {
                                        System.out.println("date2 is Greater than my date1");
                                        list.add(ngo);

                                    }
                                    else if(date1.compareTo(date2)==0)
                                    {
                                        Log.d("timeequal","hua");
                                        try {
                                            String string1 = ngo.getDatetime().substring(ngo.getDatetime().indexOf(' ')+1);
                                            Date time1 = new SimpleDateFormat("HH:mm").parse(string1);
                                            Calendar calendar1 = Calendar.getInstance();
                                            calendar1.setTime(time1);
                                            calendar1.add(Calendar.DATE, 1);



                                            String currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                                            Log.d("current date",currentDateAndTime);

                                            String someRandomTime =currentDateAndTime.substring(currentDateAndTime.indexOf(' ')+1);
                                            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
                                            Calendar calendar3 = Calendar.getInstance();
                                            calendar3.setTime(d);
                                            calendar3.add(Calendar.DATE, 1);

                                            Date x = calendar3.getTime();
                                            if (x.before(calendar1.getTime()) ) {
                                                //checkes whether the current time is between 14:49:00 and 20:11:13.
                                                list.add(ngo);



                                            }
                                            else
                                            {

                                                Log.d("timeequal","false");
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            Log.d("timeequal", String.valueOf(e));
                                        }

                                    }


                                }catch (ParseException e1){
                                    e1.printStackTrace();
                                }

                                //if(i+1<lister.size())

                            }

                            }
                           // list=response.body().getId().get(l).getActivities();

                            //Log.d("ngolis","mil gya"+list.size());
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
