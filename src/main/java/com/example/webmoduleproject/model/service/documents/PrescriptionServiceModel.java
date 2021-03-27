package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;

import java.time.LocalDate;

public class PrescriptionServiceModel extends DocumentDetails {
    private LocalDate date;
    private String medicines;

    public PrescriptionServiceModel() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }
}
