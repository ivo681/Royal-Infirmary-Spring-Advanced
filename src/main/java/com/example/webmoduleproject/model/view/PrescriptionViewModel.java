package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PrescriptionViewModel extends DocumentDetails {
    private LocalDate date;
    private String medicines;

    public PrescriptionViewModel() {
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
