package com.suvidha.Models;

public class AcceptModel {
    public String oid;
    public String sid;
    public int status;

    public AcceptModel(String oid, String sid, int status) {
        this.oid = oid;
        this.sid = sid;
        this.status = status;
    }
}
