package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.model.view.commonDetails.AppointmentDetails;

import java.time.LocalDate;

public class AppointmentConfirmViewModel extends AppointmentDetails {
    private String userEmail;
    private String md_id;
    private String mdFullName;

    public AppointmentConfirmViewModel() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMd_id() {
        return md_id;
    }

    public void setMd_id(String md_id) {
        this.md_id = md_id;
    }

    public String getMdFullName() {
        return mdFullName;
    }

    public void setMdFullName(String mdFullName) {
        this.mdFullName = mdFullName;
    }

}
