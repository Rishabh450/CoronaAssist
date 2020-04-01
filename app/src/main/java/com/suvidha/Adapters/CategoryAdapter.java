package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suvidha.Activities.ItemActivity;
import com.suvidha.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.suvidha.Utilities.Utils.catHashMap;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
    List<Integer> list;
    Context context;
    String shopid;
    public CategoryAdapter(Context context, List<Integer> list,String shopid) {
        this.list = list;
        this.context = context;
        this.shopid = shopid;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Integer catId = list.get(position);
        Log.e("LOL", String.valueOf(catId));
        holder.name.setText(catHashMap.get(catId+1).first);
        holder.img.setImageResource(catHashMap.get(catId+1).second);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemActivity.class);
                intent.putExtra("CategoryId",catId);
                intent.putExtra("shopid",shopid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null)return 0;
        return list.size();
    }



    public class MyHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView img;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cat_name);
            img = itemView.findViewById(R.id.cat_img);
        }
    }
}
