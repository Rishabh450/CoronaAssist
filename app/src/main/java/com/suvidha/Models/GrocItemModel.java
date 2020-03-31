package com.suvidha.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class GrocItemModel implements Parcelable {
    public String itemId;
    public String itemName;
    public String itemQty;
    public int item_add_qty;
    public double itemPrice;
    public int category;

    public GrocItemModel(String itemId, String itemName, String itemQty, int item_add_qty, double itemPrice, int category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQty = itemQty;
        this.item_add_qty = item_add_qty;
        this.itemPrice = itemPrice;
        this.category = category;
    }

    protected GrocItemModel(Parcel in) {
        itemId = in.readString();
        itemName = in.readString();
        itemQty = in.readString();
        item_add_qty = in.readInt();
        itemPrice = in.readDouble();
        category = in.readInt();
    }

    public static final Creator<GrocItemModel> CREATOR = new Creator<GrocItemModel>() {
        @Override
        public GrocItemModel createFromParcel(Parcel in) {
            return new GrocItemModel(in);
        }

        @Override
        public GrocItemModel[] newArray(int size) {
            return new GrocItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(itemName);
        dest.writeString(itemQty);
        dest.writeInt(item_add_qty);
        dest.writeDouble(itemPrice);
        dest.writeInt(category);
    }
}
