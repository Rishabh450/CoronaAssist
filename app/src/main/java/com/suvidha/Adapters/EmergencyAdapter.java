package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suvidha.R;

import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.MyHolder> {
    public Context ctx;
    public List<String> list;
    public EmergencyAdapter(Context ctx, List<String> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_emergency,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.emerNo.setText(list.get(position));
        holder.emerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position)));
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) return list.size();
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView emerNo;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            emerNo = itemView.findViewById(R.id.emer_no);
        }
    }
}
