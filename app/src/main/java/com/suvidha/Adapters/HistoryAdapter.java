package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
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
        holder.time.setText(String.valueOf(data.time));
        holder.orderid.setText(data._id);
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
        TextView shop_name,shop_addr,orderid,time;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            shop_name = itemView.findViewById(R.id.history_shopname);
            shop_addr = itemView.findViewById(R.id.history_shopaddr);
            orderid = itemView.findViewById(R.id.history_orderid);
            time = itemView.findViewById(R.id.history_time);
        }
    }
}
