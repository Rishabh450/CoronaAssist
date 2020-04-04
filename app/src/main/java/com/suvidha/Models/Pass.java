package com.suvidha.Models;

import com.google.gson.annotations.SerializedName;

public class Pass {

    @SerializedName("_id")
    public String passid;
    @SerializedName("proof")
    public String  proof;
    @SerializedName("destination")
    public String  destination;
    @SerializedName("vehicle")
    public String  vehicle;
    @SerializedName("purpose")
    public String  purpose;
    @SerializedName("time")
    public String  time;
    @SerializedName("duration")
    public String  duration;
    @SerializedName("status")
    public int status;
    @SerializedName("uid")
    public String  uid;
    @SerializedName("type")
    public int type;
    @SerializedName("senior_citizen")
    public boolean seniorCitizen;
    @SerializedName("passenger_count")
    public int passengerCount;
    @SerializedName("urgency")
    public boolean urgency;
    @SerializedName("urgency_text")
    public String urgencyText;
    @SerializedName("name")
    public String name;
    @SerializedName("date")
    public String date;



    public Pass() {
    }

    public Pass(String passid, String proof, String destination, String vehicle, String purpose, String time, String duration, int status, String uid, int type, boolean seniorCitizen, int passengerCount, boolean urgency, String urgencyText, String name, String date) {
        this.passid = passid;
        this.proof = proof;
        this.destination = destination;
        this.vehicle = vehicle;
        this.purpose = purpose;
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.uid = uid;
        this.type = type;
        this.seniorCitizen = seniorCitizen;
        this.passengerCount = passengerCount;
        this.urgency = urgency;
        this.urgencyText = urgencyText;
        this.name = name;
        this.date = date;
    }

    public Pass(String proof, String destination, String vehicle, String purpose, String time, String duration, int status, String uid, int type, boolean seniorCitizen, int passengerCount, boolean urgency, String urgencyText, String name, String date) {
        this.proof = proof;
        this.destination = destination;
        this.vehicle = vehicle;
        this.purpose = purpose;
        this.time = time;
        this.duration = duration;
        this.status = status;
        this.uid = uid;
        this.type = type;
        this.seniorCitizen = seniorCitizen;
        this.passengerCount = passengerCount;
        this.urgency = urgency;
        this.urgencyText = urgencyText;
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSeniorCitizen() {
        return seniorCitizen;
    }

    public void setSeniorCitizen(boolean seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public boolean isUrgency() {
        return urgency;
    }

    public void setUrgency(boolean urgency) {
        this.urgency = urgency;
    }

    public String getUrgencyText() {
        return urgencyText;
    }

    public void setUrgencyText(String urgencyText) {
        this.urgencyText = urgencyText;
    }

    public String getPassid() {
        return passid;
    }

    public void setPassid(String passid) {
        this.passid = passid;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
