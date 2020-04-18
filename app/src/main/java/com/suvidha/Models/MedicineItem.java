package com.suvidha.Models;

public class MedicineItem {
    private String medicineName;
    private int id;
    private int quantity;

    public MedicineItem(String medicineName, int id, int quantity) {
        this.medicineName = medicineName;
        this.id = id;
        this.quantity = quantity;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
