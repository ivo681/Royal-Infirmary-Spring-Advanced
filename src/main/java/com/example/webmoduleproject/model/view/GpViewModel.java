package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class GpViewModel extends MedicDetails {
    private Long age;
    private Long numberPatients;

    public GpViewModel() {
    }

    public Long getNumberPatients() {
        return numberPatients;
    }

    public void setNumberPatients(Long numberPatients) {
        this.numberPatients = numberPatients;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}
