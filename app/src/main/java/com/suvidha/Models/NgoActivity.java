package com.suvidha.Models;

public class NgoActivity {
   public long datetime;
   public String address;
   public String city;
   public float lat;
   public float lon;

    public NgoActivity(long datetime, String address, String city, float lat, float lon) {
        this.datetime = datetime;
        this.address = address;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
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
