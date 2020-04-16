package com.suvidha.Models;

public class SupportModel {
    public int is_quarantine;
    public int is_pass;
    public int is_ngo;
    public int is_shopper;
    public String district;
    public String state;


    public SupportModel(int is_quarantine, int is_pass, int is_ngo, int is_shopper) {
        this.is_quarantine = is_quarantine;
        this.is_pass = is_pass;
        this.is_ngo = is_ngo;
        this.is_shopper = is_shopper;
    }
}
