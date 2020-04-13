package com.suvidha.Models;

import java.util.List;

public class FetchNgomodel {
   public String status;
  public   List<NgoModel> id;

    public FetchNgomodel(String status, List<NgoModel> id) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NgoModel> getId() {
        return id;
    }

    public void setId(List<NgoModel> id) {
        this.id = id;
    }
}
