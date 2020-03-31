package com.suvidha.Models;

import java.util.ArrayList;
import java.util.List;

public class OrdersModel {
    public String orderId;

    public ArrayList<GrocItemModel> list;
    public int orderStatus;
    public ShopModel shopDetails;

    public OrdersModel(String orderId, ArrayList<GrocItemModel> list, int orderStatus, ShopModel shopDetails) {
        this.orderId = orderId;
        this.list = list;
        this.orderStatus = orderStatus;
        this.shopDetails = shopDetails;
    }

}
