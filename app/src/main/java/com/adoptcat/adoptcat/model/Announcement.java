package com.adoptcat.adoptcat.model;


import android.graphics.Bitmap;

import java.io.Serializable;

public class Announcement implements Serializable {

    private String description, date, id, title, phone, userUUID;
    private int amount;
    private double latitude, longitude;
    private boolean vaccineted, dewomed,spayed, hasPhoto;


    public Announcement() {}


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isVaccineted() {
        return vaccineted;
    }

    public void setVaccineted(boolean vaccineted) {
        this.vaccineted = vaccineted;
    }

    public boolean isDewomed() {
        return dewomed;
    }

    public void setDewomed(boolean dewomed) {
        this.dewomed = dewomed;
    }

    public boolean isSpayed() {
        return spayed;
    }

    public void setSpayed(boolean spayed) {
        this.spayed = spayed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }
}
