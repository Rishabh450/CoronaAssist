package com.suvidha.Models;

public class OrderRequestModel {
    public CartModel id;
    public int status;

    public OrderRequestModel(CartModel id, int status) {
        this.id = id;
        this.status = status;
    }
}
