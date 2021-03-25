package com.example.webmoduleproject.model.service;

import java.time.LocalDate;

public class AppointmentServiceModel {
    private String userEmail;
    private String mdId;
    private String mdFullName;
    private LocalDate date;
    private String reason;
    private String timeSpan;

    public AppointmentServiceModel() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMdId() {
        return mdId;
    }

    public void setMdId(String mdId) {
        this.mdId = mdId;
    }

    public String getMdFullName() {
        return mdFullName;
    }

    public void setMdFullName(String mdFullName) {
        this.mdFullName = mdFullName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }
}
