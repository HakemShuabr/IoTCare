package com.example.admin.firstversionapp;

/**
 * Created by Admin on 07/09/2017.
 */

public class Doctor extends User {
    private String specialization;
    private Report report;

    public Doctor(){
        super();
    }

    public Doctor(String name, String password, String email, String number, int active) {
        super(name, password, email, number, active);
        specialization = "not specified";
        report = new Report();
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setReport(Report report) {

        this.report = report;
    }

    public String getSpecialization() {

        return specialization;
    }

    public Report getReport() {
        return report;
    }
}
