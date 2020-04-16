package com.suvidha.Models;

import java.util.ArrayList;
import java.util.List;

public class SQList {
    int status;
    List<SQDetail> id=new ArrayList<>();

    public SQList(int status, List<SQDetail> id) {
        this.status = status;
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<SQDetail> getId() {
        return id;
    }

    public void setId(List<SQDetail> id) {
        this.id = id;
    }
}
