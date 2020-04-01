package com.suvidha.Models;

import java.util.ArrayList;

public class OrdersModel {
    public String orderId;

    public ArrayList<ItemModel> list;
    public int orderStatus;
    public ShopModel shopDetails;

    public OrdersModel(String orderId, ArrayList<ItemModel> list, int orderStatus, ShopModel shopDetails) {
        this.orderId = orderId;
        this.list = list;
        this.orderStatus = orderStatus;
        this.shopDetails = shopDetails;
    }

}
