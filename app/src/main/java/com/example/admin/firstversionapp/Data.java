package com.example.admin.firstversionapp;

/**
 * Created by Admin on 11/02/2018.
 */

public class Data {
    private String name;
    private String value;
    private String date;

    public Data(String name, String value, String date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }
}
