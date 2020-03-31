package com.suvidha.Models;

import org.json.JSONObject;

import java.util.List;

public class LoginResult {
    public UserModel id;
    public int status;
    public List<ZonesModel> zone;

    public LoginResult(UserModel id, int status, List<ZonesModel> zone) {
        this.id = id;
        this.status = status;
        this.zone = zone;
    }
}
