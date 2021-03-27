package com.example.webmoduleproject.model.service.documents;

import java.time.LocalDate;

public class SickLeaveBindingServiceModel {
    private String reason;
    private LocalDate fromDate;
    private LocalDate toDate;

    public SickLeaveBindingServiceModel() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
