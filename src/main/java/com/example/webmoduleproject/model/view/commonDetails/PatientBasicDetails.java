package com.example.webmoduleproject.model.view.commonDetails;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public abstract class PatientBasicDetails {
    private String firstName;
    private String lastName;
    private String telephone;
    private LocalDate dateOfBirth;
    private String address;

    public PatientBasicDetails() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
