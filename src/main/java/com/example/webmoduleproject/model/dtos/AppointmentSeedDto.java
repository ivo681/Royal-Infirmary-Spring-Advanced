package com.example.webmoduleproject.model.dtos;

import com.google.gson.annotations.Expose;

import java.time.LocalDate;

public class AppointmentSeedDto {
    @Expose
    private String patient;
    @Expose
    private String md;
    @Expose
    private LocalDate date;
    @Expose
    private String reason;
    @Expose
    private String timeSpan;
    @Expose
    private String medicines;
    @Expose
    private String diagnosis;

    public AppointmentSeedDto() {
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
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

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
