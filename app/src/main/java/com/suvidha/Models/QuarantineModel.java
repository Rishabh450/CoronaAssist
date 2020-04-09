package com.suvidha.Models;

public class QuarantineModel {
    public String uid;
    public String name;
    public String address;
    public String phone;
    public float location_lat;
    public float location_lon;
    public String authority;
    public String start_date;
    public String end_date;
    public String state;
    public String district;

    public QuarantineModel(String name, String address, String phone, float location_lat, float location_lon, String authority, String start_date, String end_date,String state,String district) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
        this.authority = authority;
        this.start_date = start_date;
        this.end_date = end_date;
        this.state = state;
        this.district = district;
    }

    public QuarantineModel(String uid, String name, String address, String phone, float location_lat, float location_lon, String authority, String start_date, String end_date) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
        this.authority = authority;
        this.start_date = start_date;
        this.end_date = end_date;
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
}
