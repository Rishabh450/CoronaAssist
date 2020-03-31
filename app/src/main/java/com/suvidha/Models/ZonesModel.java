package com.suvidha.Models;

import com.google.gson.annotations.SerializedName;

public class ZonesModel {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;

    public ZonesModel(String name,int id) {
        this.id = id;
        this.name = name;
    }
}
