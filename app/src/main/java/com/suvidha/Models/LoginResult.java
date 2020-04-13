package com.suvidha.Models;

import org.json.JSONObject;

import java.util.List;

public class LoginResult {
    public UserModel id;
    public int status;
    public List<AddressModel> available;
    public LocationModel location;

    public LoginResult(UserModel id, int status, List<AddressModel> available, LocationModel location) {
        this.id = id;
        this.status = status;
        this.available = available;
        this.location = location;
    }
}
