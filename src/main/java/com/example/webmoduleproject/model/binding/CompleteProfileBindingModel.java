package com.example.webmoduleproject.model.binding;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CompleteProfileBindingModel {
    private String idNumber;
    private String telephone;
    private String job;
    private String employer;
    private String address;

    public CompleteProfileBindingModel() {
    }

    @NotBlank(message = "ID number field cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Please enter a valid 10 digit Id number")
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @NotBlank(message = "Telephone field cannot be empty")
    @Pattern(regexp = "\\d{10,14}", message = "Please enter a valid 10 digit telephone number")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @NotBlank(message = "Address field cannot be empty")
    @Length(min = 3, message = "Address must be at least 3 characters long")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
