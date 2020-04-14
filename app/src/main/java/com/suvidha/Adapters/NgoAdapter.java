package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suvidha.Activities.OrderDetailsActivity;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.NgoActivity;
import com.suvidha.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.suvidha.Utilities.Utils.orderStatus;
import static com.suvidha.Utilities.Utils.statusHashMap;

public class NgoAdapter extends RecyclerView.Adapter<NgoAdapter.MyHolder> {
    Context ctx;
    List<NgoActivity> list;
    public NgoAdapter(Context ctx, List<NgoActivity> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_ngo,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        NgoActivity data = list.get(position);
       holder.ngo_address.setText(data.getAddress());
       holder.ngo_city.setText(data.getCity());
//       String date="At "+data.getDatetime().substring(data.getDatetime().indexOf('T')+1)+"\n"+data.getDatetime().substring(0,data.getDatetime().indexOf('T'));
        String date = data.getDatetime();
        DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd hh:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
       // Log.d("timewa",formatter.format(date));
        //System.out.println(formatter.format(date));
        holder.ngo_time.setText(date);
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView ngo_city,ngo_address,ngo_time;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
           ngo_address=itemView.findViewById(R.id.ngo_address);
           ngo_city=itemView.findViewById(R.id.ngo_city);
           ngo_time=itemView.findViewById(R.id.ngo_time);
        }
    }
}