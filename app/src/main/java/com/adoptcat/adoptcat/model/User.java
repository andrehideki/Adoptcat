package com.adoptcat.adoptcat.model;

import android.graphics.Bitmap;
import android.net.Uri;


public class User {

    private String UUID, name, email, phone, city;
    private static User user;

    private User() {}

    public static User getInstance() {
        return (user == null)? user = new User() : user;
    }

    public static void destroyUser() {
        user = null;
    }

    public static void cloneState( User u ) {
        user.setPhone( u.getPhone() );
        user.setEmail( u.getEmail() );
        user.setName( u.getName() );
        user.setCity( u.getCity() );
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
