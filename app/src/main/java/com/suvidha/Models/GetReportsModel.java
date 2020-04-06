package com.suvidha.Models;

import java.util.List;

public class GetReportsModel {
    public List<ReportModel> id;
    public int status;

    public GetReportsModel(List<ReportModel> id, int status) {
        this.id = id;
        this.status = status;
    }
}
