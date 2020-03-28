package com.chat.pcon.myapplication.Models;

public class GrocItemModel {
    public String itemId;
    public String itemName;
    public String itemQty;
    public int item_add_qty;
    public double itemPrice;
    public int category;

    public GrocItemModel(String itemId, String itemName, String itemQty, int item_add_qty, double itemPrice, int category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQty = itemQty;
        this.item_add_qty = item_add_qty;
        this.itemPrice = itemPrice;
        this.category = category;
    }
}
