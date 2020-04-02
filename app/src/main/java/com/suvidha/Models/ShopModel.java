package com.suvidha.Models;

import java.util.ArrayList;

public class ShopModel {
    public String _id;
    public String name;
    public String email;
    public String address;
    public String phone;
    public int type;
    public ArrayList<ItemModel> items;
    public ShopModel(){}

    public ShopModel(String id, String name, String email, String address, String phone, int type, ArrayList<ItemModel> items) {
        this._id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.type = type;
        this.items = items;
    }
}
