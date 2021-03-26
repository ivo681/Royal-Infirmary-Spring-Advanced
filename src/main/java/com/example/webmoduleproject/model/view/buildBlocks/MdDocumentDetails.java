package com.example.webmoduleproject.model.view.buildBlocks;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class MdDocumentDetails extends MedicDetails {
    private String telephone;
    private String email;

    public MdDocumentDetails(){}

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
