package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suvidha.Activities.CategoriesActivity;
import com.suvidha.Activities.ItemActivity;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.MedicineItem;
import com.suvidha.Models.ShopModel;
import com.suvidha.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MyHolder> {

    public Context ctx;
    public List<ItemModel> list;

    public MedicineListAdapter(Context ctx, List<ItemModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.medicineitem, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.med_name.setText(list.get(position).item_name);
        holder.quan.setText(String.valueOf(list.get(position).item_add_qty) );


    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView med_name, quan;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            med_name = itemView.findViewById(R.id.medname);
            quan = itemView.findViewById(R.id.quan);

        }
    }
}
