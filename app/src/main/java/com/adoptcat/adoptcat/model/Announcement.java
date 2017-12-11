package com.adoptcat.adoptcat.model;


import android.graphics.Bitmap;

public class Announcement {

    private String description, date, location, id;
    private int amount;
    private boolean vaccineted, dewomed,spayed;

    private Bitmap catPhoto;


    public Announcement() {}

    public Announcement(String description, String date, String location, int amount, boolean vaccineted,
                  boolean dewomed, boolean spayed) {
        this.description = description;
        this.date = date;
        this.location = location;
        this.amount = amount;
        this.vaccineted = vaccineted;
        this.dewomed = dewomed;
        this.spayed = spayed;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Bitmap getCatPhoto() {
        return catPhoto;
    }

    public void setCatPhoto(Bitmap catPhoto) {
        this.catPhoto = catPhoto;
    }
}
