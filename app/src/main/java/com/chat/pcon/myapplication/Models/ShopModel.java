package com.chat.pcon.myapplication.Models;

import java.util.List;

public class ShopModel {
    public String shopId;
    public String shopName;
    public String shopZone;
    public String shopContact;
    public List<GrocItemModel> items;

    public ShopModel(String shopId, String shopName, String shopZone, String shopContact, List<GrocItemModel> items) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopZone = shopZone;
        this.shopContact = shopContact;
        this.items = items;
    }
}
