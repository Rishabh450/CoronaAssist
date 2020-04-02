package com.suvidha.Models;

import java.util.List;

public class ItemsRequestModel {
    public List<ItemModel> id;
    public int status;

    public ItemsRequestModel(List<ItemModel> id, int status) {
        this.id = id;
        this.status = status;
    }
}
