package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.UserBasicDetails;

public class UserDetailsViewModel extends UserBasicDetails {
    private String email;
    private String job;
    private String idNumber;
    private String employer;
    private String address;

    public UserDetailsViewModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
