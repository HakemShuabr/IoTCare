package com.example.admin.firstversionapp;

/**
 * Created by Admin on 09/09/2017.
 */

public class PDU {
    private String name;
    private String password;
    private int age;
    private String phone;
    private String image;
    private String disability;

    public PDU() {
    }

    public PDU(String name, String password, int age, String phone, String disability) {
        this.name = name;
        this.password = password;
        this.age = age;
        this.phone = phone;
        this.disability = disability;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public String getDisability() {
        return disability;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }
}
