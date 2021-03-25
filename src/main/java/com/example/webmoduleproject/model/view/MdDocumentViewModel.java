package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class MdDocumentViewModel extends MedicDetails {
    private String telephone;
    private String email;

    public MdDocumentViewModel(){}

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
