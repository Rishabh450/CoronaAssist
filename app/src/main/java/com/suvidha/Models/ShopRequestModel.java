package com.suvidha.Models;

import java.util.List;

public class ShopRequestModel {
    public List<ShopModel> id;
    public int status;

    public ShopRequestModel(List<ShopModel> id, int status) {
        this.id = id;
        this.status = status;
    }
}
