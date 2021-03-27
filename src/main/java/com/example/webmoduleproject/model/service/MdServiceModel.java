package com.example.webmoduleproject.model.service;


import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

import java.time.LocalDate;

public class MdServiceModel extends MedicDetails {
    private LocalDate dateOfBirth;
    private int age;
    private String job;

    public MdServiceModel() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setPosition(String job) {
        this.job = job;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
