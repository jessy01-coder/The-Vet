package com.sanj.thevet.models;

public class VetModel {
    private String name,phone,location,licenseNumber,category,nid;

    public VetModel() {
    }

    public VetModel(String name, String phone, String location, String licenseNumber, String category, String nid) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.licenseNumber = licenseNumber;
        this.category = category;
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getNid() {
        return nid;
    }
}
