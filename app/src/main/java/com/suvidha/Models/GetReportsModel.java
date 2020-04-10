package com.suvidha.Models;

import java.util.List;

public class GetReportsModel {
    public List<ReportModel> id;
    public int status;
    public int left;

    public GetReportsModel(List<ReportModel> id, int status,int left) {
        this.id = id;
        this.status = status;
        this.left = left;
    }
}
