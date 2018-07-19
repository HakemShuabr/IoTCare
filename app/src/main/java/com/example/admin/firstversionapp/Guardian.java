package com.example.admin.firstversionapp;

/**
 * Created by Admin on 07/09/2017.
 */

public class Guardian extends User {
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {

        return relationship;
    }

    private String relationship;

    public Guardian() {}

    public Guardian(String name, String password, String email, String number, int active, String relationship) {
        super(name, password, email, number, active);
        this.relationship = relationship;
    }
}
