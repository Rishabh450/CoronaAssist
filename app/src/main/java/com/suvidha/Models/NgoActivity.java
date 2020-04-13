package com.suvidha.Models;

public class NgoActivity {
   public String datetime;
   public String address;
   public String city;
   public float lat;
   public float lon;

    public NgoActivity(String datetime, String address, String city, float lat, float lon) {
        this.datetime = datetime;
        this.address = address;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }

    public String  getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
