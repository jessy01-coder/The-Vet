package com.sanj.thevet.models;

public class AppointmentModel {
    private String name,phone,location,farmerNID,appointmentId,status,date,category;

    public AppointmentModel() {
    }

    public AppointmentModel(String name, String phone, String location, String farmerNID, String appointmentId, String status,String date,String category) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.farmerNID = farmerNID;
        this.appointmentId = appointmentId;
        this.status=status;
        this.date=date;
        this.category=category;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getFarmerNID() {
        return farmerNID;
    }

    public String getAppointmentId() {
        return appointmentId;
    }
}
