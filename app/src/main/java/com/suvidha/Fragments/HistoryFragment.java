package com.suvidha.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.suvidha.Adapters.HistoryAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GetOrdersModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.getAccessToken;

public class HistoryFragment extends Fragment {
    RecyclerView rview;
    private ApiInterface apiInterface;
    List<CartModel> data = new ArrayList<>();
    HistoryAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history,container,false);
        initRetrofit();
        getData();
        rview = v.findViewById(R.id.history_rview);
        setRecyclerView();
        return v;
    }

    private void getData() {
        Call<GetOrdersModel> listCall =  apiInterface.getAllOrders(getAccessToken(getContext()));
        listCall.enqueue(new Callback<GetOrdersModel>() {
            @Override
            public void onResponse(Call<GetOrdersModel> call, Response<GetOrdersModel> response) {
                if(response.body().status == 200){
                    data.clear();
                    data.addAll(response.body().id);
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(), "Failed to get your history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetOrdersModel> call, Throwable t) {
                Log.e("LOL",t.getMessage());
            }
        });

    }

    private void initRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void setRecyclerView() {
        rview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HistoryAdapter(getContext(),data);
        rview.setAdapter(mAdapter);
    }
}
