package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentListDetails;

import java.time.LocalDate;

public class SickLeaveListAllServiceModel extends DocumentListDetails {
    private LocalDate fromDate;
    private LocalDate toDate;

    public SickLeaveListAllServiceModel() {
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
