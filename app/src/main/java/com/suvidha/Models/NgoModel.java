package com.suvidha.Models;

import org.json.JSONArray;

import java.util.List;

public class NgoModel

{
 public String name;
public List<String> phone_number;
public List<NgoActivity> activities;

    public NgoModel(String name, List<String> phone_number, List<NgoActivity> activities) {
        this.name = name;
        this.phone_number = phone_number;
        this.activities = activities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(List<String> phone_number) {
        this.phone_number = phone_number;
    }

    public List<NgoActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<NgoActivity> activities) {
        this.activities = activities;
    }
}
