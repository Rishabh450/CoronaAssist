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

    public QuarantineModel(String name, String address, String phone, float location_lat, float location_lon, String authority, String start_date, String end_date) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
        this.authority = authority;
        this.start_date = start_date;
        this.end_date = end_date;
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
}
