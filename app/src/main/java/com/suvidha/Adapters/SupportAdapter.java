package com.suvidha.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suvidha.Fragments.HomeFragment;
import com.suvidha.Models.HomeIconModel;
import com.suvidha.R;

import java.util.HashMap;
import java.util.List;

import static com.suvidha.Utilities.Utils.is_quarantined;

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.MyHolder> {
    List<HomeIconModel> hm;
    HomeCallback mCallback;
    HomeFragment homeFragment;
    Context ctx;
    public SupportAdapter(HomeFragment homeFragment, List<HomeIconModel> hm) {
        this.hm = hm;
        this.homeFragment = homeFragment;
        mCallback = (HomeCallback) homeFragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_icon,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        HomeIconModel data = hm.get(position);

        holder.iv.setImageResource(data.res_img);
        holder.tv.setText(data.res_txt);
        if(data.pos == 0 && is_quarantined==0){
            holder.tv.setText(ctx.getResources().getString(R.string.register_quarantine));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onIconClick(data.pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(hm == null) return 0;
        return hm.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.home_icon_img);
            tv = itemView.findViewById(R.id.home_icon_text);
        }
    }
    public interface HomeCallback{
        void onIconClick(int pos);
    }
}
