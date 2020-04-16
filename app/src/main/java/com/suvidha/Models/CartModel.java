package com.suvidha.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class CartModel implements Parcelable {
    public String _id;
    public ArrayList<ItemModel> items;
    public String sid;
    public double amount;
    public int status;
    public String uid;
    public String time;
    public String delivery_time;
    public ShopModel shop_details;
    public String address;


    public CartModel(String _id, ArrayList<ItemModel> items, String sid, double amount, int status, String uid, ShopModel shop_details, String address) {
        this._id = _id;
        this.items = items;
        this.sid = sid;
        this.amount = amount;
        this.status = status;
        this.uid = uid;
        this.time = time;
        this.shop_details = shop_details;
        this.address = address;
    }

    public CartModel(){}

    public CartModel(ArrayList<ItemModel> items, String sid, double amount, int status, String address) {
        this._id = _id;
        this.items = items;
        this.sid = sid;
        this.amount = amount;
        this.status = status;
        this.uid = uid;
        this.time = time;
        this.shop_details = shop_details;
        this.address = address;
    }

    public CartModel(String _id, ArrayList<ItemModel> items, String sid, double amount, int status, String uid, String time, ShopModel shop_details) {
        this._id = _id;
        this.items = items;
        this.sid = sid;
        this.amount = amount;
        this.status = status;
        this.uid = uid;
        this.time = time;
        this.shop_details = shop_details;
    }

    public CartModel(ArrayList<ItemModel> items, String sid, double amount, int status, String uid, String time, ShopModel shopDetails) {
        this.items = items;
        this.sid = sid;
        this.amount = amount;
        this.status = status;
        this.uid = uid;
        this.time = time;
        this.shop_details = shopDetails;
    }


    protected CartModel(Parcel in) {
        items = in.createTypedArrayList(ItemModel.CREATOR);
        sid = in.readString();
        amount = in.readDouble();
        status = in.readInt();
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(items);
        dest.writeString(sid);
        dest.writeDouble(amount);
        dest.writeInt(status);
    }


}
