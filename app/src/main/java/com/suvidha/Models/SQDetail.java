package com.suvidha.Models;

public class SQDetail {
    public double lat,lon;
    String address;
    String phone;

    public SQDetail(double lat, double lon, String address, String phone) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.phone = phone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
