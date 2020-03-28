package com.chat.pcon.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.pcon.myapplication.Models.GrocItemModel;
import com.chat.pcon.myapplication.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyHolder> {
    private Context context;
    private List<GrocItemModel> list;

    public ItemAdapter(Context context, List<GrocItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        GrocItemModel item = list.get(position);
        holder.itemName.setText(item.itemName);
        holder.itemQty.setText(item.itemQty);
        holder.price.setText("Rs. "+item.itemPrice);
        holder.addLayout.setVisibility(View.GONE);
        holder.itemBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty+=1;
                holder.itemBtnAdd.setVisibility(View.GONE);
                holder.addLayout.setVisibility(View.VISIBLE);
                holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
            }
        });
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty+=1;
                holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
            }
        });
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty-=1;
                if(item.item_add_qty==0){
                    holder.itemBtnAdd.setVisibility(View.VISIBLE);
                    holder.addLayout.setVisibility(View.GONE);
                }
                holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public LinearLayout addLayout;
        public TextView itemName;
        public TextView itemQty;
        public TextView itemAddQty;
        public Button itemBtnAdd;
        public Button plusBtn,minusBtn;
        public TextView price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            addLayout = itemView.findViewById(R.id.add_layout);
            itemName = itemView.findViewById(R.id.item_name);
            itemQty = itemView.findViewById(R.id.item_qty);
            itemBtnAdd = itemView.findViewById(R.id.item_add_btn);
            plusBtn = itemView.findViewById(R.id.item_plus_btn);
            minusBtn = itemView.findViewById(R.id.item_minus_btn);
            price = itemView.findViewById(R.id.item_price);
            itemAddQty = itemView.findViewById(R.id.item_add_qty);
        }
    }
}
