package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.MedicDetails;

public class MdViewModel extends MedicDetails {
    private Long age;
    private String job;

    public MdViewModel() {
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}
