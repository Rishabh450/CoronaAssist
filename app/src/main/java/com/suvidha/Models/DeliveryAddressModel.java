package com.suvidha.Models;

public class DeliveryAddressModel {
    public String city;
    public String zone;
    public String subzone;
    public String sector;
    public String area;

    public DeliveryAddressModel(String city, String zone, String subzone, String sector, String area) {
        this.city = city;
        this.zone = zone;
        this.subzone = subzone;
        this.sector = sector;
        this.area = area;
    }
}
