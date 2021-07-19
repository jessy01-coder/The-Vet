package com.sanj.thevet.models;

public class Loc {
    String latitude,longitude,uid,phone;
    public Loc()
    {

    }

    public Loc(String latitude, String longitude, String uid, String phone) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.uid = uid;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
