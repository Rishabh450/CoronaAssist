package com.suvidha.Models;

import com.google.gson.annotations.SerializedName;

public class PassGenerationResult {

    @SerializedName("id")
    public String id;
    @SerializedName("status")
    public Integer status;

    public PassGenerationResult() {
    }


    public PassGenerationResult(String id, Integer status) {
        super();
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
