package com.suvidha.Models;

import java.util.List;

public class GetOrdersModel {
    public List<CartModel> id;
    public int status;

    public GetOrdersModel(List<CartModel> id, int status) {
        this.id = id;
        this.status = status;
    }
}
