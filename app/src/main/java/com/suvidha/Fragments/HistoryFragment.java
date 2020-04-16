package com.suvidha.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.suvidha.Activities.MainActivity;
import com.suvidha.Adapters.HistoryAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GetOrdersModel;
import com.suvidha.Models.ReportModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.allOrders;
import static com.suvidha.Utilities.Utils.getAccessToken;

public class HistoryFragment extends Fragment implements MainActivity.NotifyFragment {
    RecyclerView rview;
    private ApiInterface apiInterface;
    List<CartModel> data = new ArrayList<>();
    HistoryAdapter mAdapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history,container,false);
        initRetrofit();
        rview = v.findViewById(R.id.history_rview);
        setRecyclerView();
        getData();
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData() {
        data.clear();
        data.addAll(allOrders);
        data.sort(new TimestampSorter());
        mAdapter.notifyDataSetChanged();
    }

    private void initRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void setRecyclerView() {
        rview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HistoryAdapter(getContext(),data);
        rview.setAdapter(mAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyDataLoaded() {
        data.clear();
        data.addAll(allOrders);
        data.sort(new TimestampSorter());
        mAdapter.notifyDataSetChanged();
    }
    public class TimestampSorter implements Comparator<CartModel>
    {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        @Override
        public int compare(CartModel o1, CartModel o2) {
            try {
                if(f.parse(o2.time).before(f.parse(o1.time))){
                    return -10;
                }else{
                    return 10;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
