package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suvidha.Activities.OrderDetailsActivity;
import com.suvidha.Models.CartModel;
import com.suvidha.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.suvidha.Utilities.Utils.orderStatus;
import static com.suvidha.Utilities.Utils.statusHashMap;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyHolder> {
    Context ctx;
    List<CartModel> list;
    public HistoryAdapter(Context ctx, List<CartModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_history,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        CartModel data = list.get(position);
        holder.shop_name.setText(data.shop_details.name);
        holder.shop_addr.setText(data.shop_details.address);
        Date timestamp = data.time;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        String date = formatter.format(timestamp);
        formatter = new SimpleDateFormat("hh:mm:ss a");
        String time =formatter.format(timestamp);
        holder.time.setText(String.valueOf(date + " at " + time));
        holder.orderid.setText(data._id);
        Log.e("status", String.valueOf(data.status));
        holder.order_status.setText(orderStatus.get(statusHashMap.get(data.status)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, OrderDetailsActivity.class);
                intent.putExtra("data",data);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView shop_name,shop_addr,orderid,time,order_status;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            shop_name = itemView.findViewById(R.id.history_shopname);
            shop_addr = itemView.findViewById(R.id.history_shopaddr);
            orderid = itemView.findViewById(R.id.history_orderid);
            time = itemView.findViewById(R.id.history_time);
            order_status = itemView.findViewById(R.id.history_orderstatus);
        }
    }
}
