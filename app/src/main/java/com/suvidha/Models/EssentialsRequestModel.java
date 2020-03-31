package com.suvidha.Models;

public class EssentialsRequestModel {
    public EssentialsModel id;
    public int status;

    public EssentialsRequestModel(EssentialsModel id, int status) {
        this.id = id;
        this.status = status;
    }
}
