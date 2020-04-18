package com.suvidha.Models;

import java.util.ArrayList;

public class ShopModel {
    public String _id;
    public String shop_name;
    public String email;
    public String address;
    public String phone;
    public String type;
    public ArrayList<ItemModel> items;
    public ShopModel(){}

    public ShopModel(String _id, String name, String email, String address, String phone, String type, ArrayList<ItemModel> items) {
        this._id = _id;
        this.shop_name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.type = type;
        this.items = items;
    }
}
