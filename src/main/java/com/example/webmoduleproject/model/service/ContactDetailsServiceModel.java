package com.example.webmoduleproject.model.service;

public class ContactDetailsServiceModel {
    private String newTelephone;
    private String newAddress;

    public ContactDetailsServiceModel() {
    }

    public String getNewTelephone() {
        return newTelephone;
    }

    public void setNewTelephone(String newTelephone) {
        this.newTelephone = newTelephone;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }
}
