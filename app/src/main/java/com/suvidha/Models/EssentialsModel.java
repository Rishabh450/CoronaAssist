package com.suvidha.Models;

import java.util.List;

public class EssentialsModel {
    public double delivery_cost;
    public double cess_rate;
    public List<ZonesModel> zones;
    public List<ShopTypesModel> shop_types;

    public EssentialsModel(double delivery_cost, double cess_rate, List<ZonesModel> zones, List<ShopTypesModel> shop_types) {
        this.delivery_cost = delivery_cost;
        this.cess_rate = cess_rate;
        this.zones = zones;
        this.shop_types = shop_types;
    }

}
