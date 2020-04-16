package com.suvidha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suvidha.Models.ItemModel;
import com.suvidha.R;
import com.suvidha.Utilities.CartHandler;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyHolder> {
    private Context context;
    private List<ItemModel> list;
    private CartHandler cartHandler;
    private Callback mCallback;

    public ItemAdapter(Context context, List<ItemModel> list) {
        this.context = context;
        this.list = list;
        cartHandler = CartHandler.getInstance();
        mCallback = (Callback) context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ItemModel item;
        ItemModel existingItem=null;
        if(cartHandler.getItemsCount()!=0){
            mCallback.updateGotoCart();
            existingItem = cartHandler.findItem(list.get(position));
            if(existingItem != null) {
                item = existingItem;
            }else{
                item = list.get(position);
                holder.addLayout.setVisibility(View.GONE);
                holder.itemBtnAdd.setVisibility(View.VISIBLE);
            }
        }else{
            item = list.get(position);
            holder.addLayout.setVisibility(View.GONE);
            holder.itemBtnAdd.setVisibility(View.VISIBLE);
        }
        holder.itemName.setText(item.item_name);
//        holder.itemQty.setText(item.itemQty);
        holder.price.setText("Rs. "+item.itemPrice);
        holder.addLayout.setVisibility(View.GONE);
        if(item.equals(existingItem)){
            updateCartItems(existingItem, holder);
        }
        holder.itemBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty+=1;
                holder.itemBtnAdd.setVisibility(View.GONE);
                holder.addLayout.setVisibility(View.VISIBLE);
                holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
                cartHandler.addItem(item);
                mCallback.updateGotoCart();

            }
        });
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty+=1;
                holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
                cartHandler.updateItem(item);
                mCallback.updateGotoCart();
            }
        });
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.item_add_qty -= 1;
                if(item.item_add_qty==0){
                    holder.itemBtnAdd.setVisibility(View.VISIBLE);
                    holder.addLayout.setVisibility(View.GONE);
                    holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
                    cartHandler.removeItem(item);
                }else {
                    holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
                    cartHandler.updateItem(item);
                }
                mCallback.updateGotoCart();
            }
        });
    }

    private void updateCartItems(ItemModel item, MyHolder holder) {
        holder.itemBtnAdd.setVisibility(View.GONE);
        holder.addLayout.setVisibility(View.VISIBLE);
        holder.itemAddQty.setText(String.valueOf(item.item_add_qty));
        mCallback.updateGotoCart();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public LinearLayout addLayout;
        public TextView itemName;
        public TextView itemQty;
        public TextView itemAddQty;
        public Button itemBtnAdd;
        public TextView plusBtn,minusBtn;
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
    public interface Callback{
        void updateGotoCart();
    }
}
