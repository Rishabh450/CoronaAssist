package com.suvidha.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class UserModel implements Parcelable {

    public String id;
    public String name;
    public String email;
    public String phone;
    public String address = "";
    public String zone = "";
    public String state;
    public String district;
    public String token;
    public List<String> orders;
    public List<String> passes;

    public UserModel() {
    }

    public UserModel(String name, String email, String phone, String address, String zone, String state, String district) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.zone = zone;
        this.state = state;
        this.district = district;
    }

    public UserModel(String name, String email, List<String> orders, List<String> passes) {
        this.name = name;
        this.email = email;
        this.orders = orders;
        this.passes = passes;
    }

    public UserModel(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserModel(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    protected UserModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        zone = in.readString();
        token = in.readString();
        state = in.readString();
        district = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(zone);
        dest.writeString(token);
        dest.writeString(state);
        dest.writeString(district);
    }
}
