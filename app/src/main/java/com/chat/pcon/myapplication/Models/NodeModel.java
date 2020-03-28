package com.chat.pcon.myapplication.Models;

import android.location.Location;

public class NodeModel {
    public String name;
    public Location location;
    public Double distance;

    public NodeModel(String name, Location location) {
        this.name = name;
        this.location = location;
        distance = Double.valueOf(0);
    }

    public NodeModel(String name, Location location, Double distance) {
        this.name = name;
        this.location = location;
        this.distance = distance;
    }
}
