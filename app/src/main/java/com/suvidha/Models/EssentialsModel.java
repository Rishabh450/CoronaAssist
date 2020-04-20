package com.suvidha.Models;

import java.util.List;

public class EssentialsModel {
    public double delivery_cost;
    public double cess_rate;
    public int is_quarantined;
    public SupportModel support;
    public List<String> emergency_contact;
    public List<String> state_q_address;
    public List<CityModel> city;
    public int is_delivery;
    public String version;
    public String link;
    public List<AddressModel> available;
    public EssentialsModel(double delivery_cost, double cess_rate, int is_quarantined, SupportModel support) {
        this.delivery_cost = delivery_cost;
        this.cess_rate = cess_rate;
        this.is_quarantined = is_quarantined;
        this.support = support;
    }
}
