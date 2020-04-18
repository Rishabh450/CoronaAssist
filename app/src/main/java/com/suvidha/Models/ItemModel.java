package com.suvidha.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemModel implements Parcelable {
    public String item_id;
    public String item_name;
    public int item_add_qty;
    public double itemPrice;
    public int hide;
    public String category;
    public String subcategory;
    public int order_no;


    protected ItemModel(Parcel in) {
        item_id = in.readString();
        item_name = in.readString();
        item_add_qty = in.readInt();
        itemPrice = in.readDouble();
        hide = in.readInt();
        category = in.readString();
        subcategory = in.readString();
    }

    public ItemModel(String item_id, String item_name, int item_add_qty,int hide,double itemPrice) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_add_qty = item_add_qty;
        this.hide = hide;
        this.itemPrice = itemPrice;
    }

    public ItemModel(int order_no, String item_name, int item_add_qty) {
        this.order_no = order_no;
        this.item_name = item_name;
        this.item_add_qty = item_add_qty;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item_id);
        dest.writeString(item_name);
        dest.writeInt(item_add_qty);
        dest.writeDouble(itemPrice);
        dest.writeInt(hide);
        dest.writeString(category);
        dest.writeString(subcategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemModel> CREATOR = new Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel in) {
            return new ItemModel(in);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };
}
