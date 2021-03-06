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
import com.suvidha.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.shop_name.setText(data.shop_details.shop_name);
        holder.shop_addr.setText(data.shop_details.address);

        holder.time.setText(data.time);
        holder.orderid.setText(data._id);
        Log.e("status", String.valueOf(data.status));
        if(data.status == -1){
            holder.order_status.setTextColor(Color.RED);
        }else if(data.status == 2 || data.status == 1){
            holder.order_status.setTextColor(ctx.getResources().getColor(R.color.default_button_color));
        }
        else{
            holder.order_status.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryDark));
        }
        holder.order_status.setText(statusHashMap.get(data.status));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, OrderDetailsActivity.class);
                intent.putExtra("data",data);
                intent.putExtra("oid",data._id);
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
