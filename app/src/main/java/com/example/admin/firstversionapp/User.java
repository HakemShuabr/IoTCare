package com.example.admin.firstversionapp;

import android.net.Uri;

/**
 * Created by Admin on 01/09/2017.
 */

public class User {
    private String name;
    private String email;
    private String number;
    private String password;
    private String image;
    private int active;


    public User() {
    }

    public User(String name, String password, String email, String number, int active) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.number = number;
        this.active = active;
        this.image = "null";
    }

    public String getName() {

        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }


    public int getActive() {

        return active;
    }

    public String getImage() {
        return image;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setImage(String image) {
        this.image = image;
    }
}