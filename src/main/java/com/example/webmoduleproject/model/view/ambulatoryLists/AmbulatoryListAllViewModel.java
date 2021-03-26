package com.example.webmoduleproject.model.view.ambulatoryLists;

import com.example.webmoduleproject.model.view.commonDetails.DocumentListDetails;

import java.time.LocalDate;

public class AmbulatoryListAllViewModel extends DocumentListDetails {
    private LocalDate date;
    private String job;
    private String diagnosis;

    public AmbulatoryListAllViewModel() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
