package com.example.webmoduleproject.model.view;
import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;

import java.time.LocalDate;

public class AmbulatoryListViewModel extends DocumentDetails {
    private String id;
    private LocalDate date;
    private String medicines;

    public AmbulatoryListViewModel() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
