package com.example.webmoduleproject.model.view;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PatientPrescriptionViewModel {
    private String firstName;
    private String lastName;
    private String address;
    private LocalDate dateOfBirth;

    public PatientPrescriptionViewModel() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
