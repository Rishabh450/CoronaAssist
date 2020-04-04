package com.suvidha.Models;


import java.sql.Timestamp;

public class ReportModel {
    public String img;
    public float location_lat;
    public float location_lon;
    public Timestamp report_time;
    public int location_error;

    public ReportModel(String img, float location_lat, float location_lon, Timestamp report_time, int location_error) {
        this.img = img;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
        this.report_time = report_time;
        this.location_error = location_error;
    }


}
