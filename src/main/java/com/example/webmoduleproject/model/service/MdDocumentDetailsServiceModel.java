package com.example.webmoduleproject.model.service;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class MdDocumentDetailsServiceModel extends MedicDetails {
    private String telephone;
    private String email;

    public MdDocumentDetailsServiceModel() {
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
