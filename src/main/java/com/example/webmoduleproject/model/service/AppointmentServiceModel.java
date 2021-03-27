package com.example.webmoduleproject.model.service;

import java.time.LocalDate;

public class AppointmentServiceModel {
    private String userEmail;
    private String md_Id;
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

    public String getMd_Id() {
        return md_Id;
    }

    public void setMd_Id(String md_Id) {
        this.md_Id = md_Id;
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
