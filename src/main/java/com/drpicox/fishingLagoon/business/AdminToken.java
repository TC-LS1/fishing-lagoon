package com.drpicox.fishingLagoon.business;

public class AdminToken {

    private String value;

    public AdminToken(String value) {
        this.value = value;
    }

    public boolean validate(AdminToken other) {
        return value.equals(other.value);
    }

}
