package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;

import java.time.LocalDate;

public class AmbulatoryListServiceModel extends DocumentDetails {
    private String id;
    private LocalDate date;
    private String diagnosis;
    private String medicines;

    public AmbulatoryListServiceModel() {
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
