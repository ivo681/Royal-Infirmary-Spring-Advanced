package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;

import java.time.LocalDate;

public class SickLeaveServiceModel extends DocumentDetails {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String patientEmployer;
    private String patientJob;
    private String reason;

    public SickLeaveServiceModel() {
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

    public String getPatientEmployer() {
        return patientEmployer;
    }

    public void setPatientEmployer(String patientEmployer) {
        this.patientEmployer = patientEmployer;
    }

    public String getPatientJob() {
        return patientJob;
    }

    public void setPatientJob(String patientJob) {
        this.patientJob = patientJob;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
