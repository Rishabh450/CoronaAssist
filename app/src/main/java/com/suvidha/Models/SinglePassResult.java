package com.suvidha.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SinglePassResult {

    @SerializedName("id")
    Pass pass;
    @SerializedName("status")
    int status;

    public SinglePassResult() {
    }

    public SinglePassResult(Pass pass, int status) {
        this.pass = pass;
        this.status = status;
    }

    public Pass getPass() {
        return pass;
    }

    public void setPass(Pass pass) {
        this.pass = pass;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
