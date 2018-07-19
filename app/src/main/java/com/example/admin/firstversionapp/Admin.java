package com.example.admin.firstversionapp;

import android.media.Image;

/**
 * Created by Admin on 08/09/2017.
 */

public class Admin {
    private String name;
    private String password;
    private String email;
    private String phone;
    private String image;

    public Admin(){
        email = "not specified";
        phone = "not specified";
        image = "null";
    }

    public Admin(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Admin(String name, String password, String email, String phone) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }
}
