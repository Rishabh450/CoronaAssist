package com.suvidha.Models;

import java.util.ArrayList;
import java.util.List;

public class ShopModel {
    public String _id;
    public String name;
    public String email;
    public String address;
    public String phone;
    public ArrayList<String> orders;
    public int zone;
    public int type;
    public ArrayList<GrocItemModel> items;
    public ShopModel(){}

    public ShopModel(String id, String name, String email, String address, String phone, int zone, int type, ArrayList<GrocItemModel> items) {
        this._id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.zone = zone;
        this.type = type;
        this.items = items;
    }
}
