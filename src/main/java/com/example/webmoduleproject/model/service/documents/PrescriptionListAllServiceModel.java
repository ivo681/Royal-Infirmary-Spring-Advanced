package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;
import com.example.webmoduleproject.model.view.commonDetails.DocumentListDetails;

import java.time.LocalDate;

public class PrescriptionListAllServiceModel extends DocumentListDetails {
    private LocalDate date;
    private String diagnosis;

    public PrescriptionListAllServiceModel() {
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
}
