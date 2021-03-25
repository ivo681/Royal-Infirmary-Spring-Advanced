package com.example.webmoduleproject.model.service;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class GpServiceModel extends MedicDetails {
    private Long age;
    private Long numberPatients;

    public GpServiceModel() {
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getNumberPatients() {
        return numberPatients;
    }

    public void setNumberPatients(Long numberPatients) {
        this.numberPatients = numberPatients;
    }
}
