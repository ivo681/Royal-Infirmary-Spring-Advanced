package com.example.webmoduleproject.model.view.prescriptions;

import com.example.webmoduleproject.model.view.commonDetails.DocumentListDetails;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PrescriptionListAllViewModel extends DocumentListDetails {
    private LocalDate date;
    private String diagnosis;

    public PrescriptionListAllViewModel() {
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
