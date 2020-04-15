package com.suvidha.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubCategoryModel implements Parcelable {
    public String cat_name;
    public String cat_id;

    protected SubCategoryModel(Parcel in) {
        cat_name = in.readString();
        cat_id = in.readString();
    }

    public static final Creator<SubCategoryModel> CREATOR = new Creator<SubCategoryModel>() {
        @Override
        public SubCategoryModel createFromParcel(Parcel in) {
            return new SubCategoryModel(in);
        }

        @Override
        public SubCategoryModel[] newArray(int size) {
            return new SubCategoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cat_name);
        parcel.writeString(cat_id);
    }
}
