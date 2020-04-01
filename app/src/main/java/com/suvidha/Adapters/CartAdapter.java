package com.suvidha.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyHolder> {
    private Context ctx;
    private List<ItemModel> list;
    private CartHandler cartHandler;
    private CartCallback mCallback;
    private boolean orderPlaced;
    public CartAdapter(Context ctx, List<ItemModel> list, boolean orderPlaced) {
        this.ctx = ctx;
        this.list = list;
        this.orderPlaced = orderPlaced;
        cartHandler = CartHandler.getInstance();
        mCallback = (CartCallback) ctx;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_cart,parent,false);
        return new MyHolder(v);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ItemModel data = list.get(position);
        holder.item_name.setText(data.itemName);
        holder.item_price.setText("\u20B9"+data.itemPrice*data.item_add_qty);
        holder.item_qty.setText(String.valueOf(data.item_add_qty));
        if(orderPlaced){
            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);
            holder.chngLayout.setBackground(null);
            holder.item_qty.setTextColor(Color.parseColor("#808080"));
        }else{
            holder.plus.setVisibility(View.VISIBLE);
            holder.minus.setVisibility(View.VISIBLE);
            holder.chngLayout.setBackground(ctx.getResources().getDrawable(R.drawable.btn_shape));
        }
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel newItem = cartHandler.findItem(data);
                newItem.item_add_qty += 1;
                cartHandler.updateItem(newItem);
                holder.item_qty.setText(String.valueOf(newItem.item_add_qty));
                holder.item_price.setText("\u20B9"+newItem.itemPrice*newItem.item_add_qty);
                mCallback.updatePrice();
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel newItem = cartHandler.findItem(data);
                if(newItem!=null) {
                    newItem.item_add_qty -= 1;
                    if (newItem.item_add_qty == 0) {
                        cartHandler.removeItem(data);
//                    list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                        if (cartHandler.getItemsCount() == 0) {
                            //close btm sheet
                            mCallback.notifyItemAdapter(newItem.itemId);
                            mCallback.closeBtmSheet();
                            mCallback.hideGoto();
                        }else{
                            mCallback.notifyItemAdapter(newItem.itemId);
                        }
                    } else {
                        cartHandler.updateItem(newItem);
                        holder.item_qty.setText(String.valueOf(newItem.item_add_qty));
                        holder.item_price.setText("\u20B9" + newItem.itemPrice * newItem.item_add_qty);
                        mCallback.updatePrice();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list==null)return 0;
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView item_name;
        TextView item_price;
        TextView item_qty;
        Button plus,minus;
        LinearLayout chngLayout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.cart_item_name);
            item_price = itemView.findViewById(R.id.cart_item_price);
            item_qty = itemView.findViewById(R.id.cart_add_qty);
            plus = itemView.findViewById(R.id.cart_plus_btn);
            minus = itemView.findViewById(R.id.cart_minus_btn);
            chngLayout = itemView.findViewById(R.id.cart_change_qty_layout);
        }
    }

    public interface CartCallback{
        void closeBtmSheet();
        void updatePrice();
        void notifyItemAdapter(String id);
        void hideGoto();
    }
}
