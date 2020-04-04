package com.suvidha.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserPassesResult {

    @SerializedName("id")
    public List<Pass> passes = new ArrayList<>();
    @SerializedName("status")
    public int status;

    public UserPassesResult() {
    }

    public UserPassesResult(List<Pass> passes, int status) {
        this.passes = passes;
        this.status = status;
    }

    public List<Pass> getPasses() {
        return passes;
    }

    public void setPasses(List<Pass> passes) {
        this.passes = passes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
