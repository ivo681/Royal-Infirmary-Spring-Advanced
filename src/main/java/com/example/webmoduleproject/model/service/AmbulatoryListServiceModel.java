package com.example.webmoduleproject.model.service;

public class AmbulatoryListServiceModel {
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
}
