package com.example.webmoduleproject.model.service.documents;

import com.example.webmoduleproject.model.view.commonDetails.DocumentDetails;

import java.time.LocalDate;

public class SickLeaveServiceModel extends DocumentDetails {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String patientCurrentEmployer;
    private String patientCurrentJob;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPatientCurrentEmployer() {
        return patientCurrentEmployer;
    }

    public void setPatientCurrentEmployer(String patientCurrentEmployer) {
        this.patientCurrentEmployer = patientCurrentEmployer;
    }

    public String getPatientCurrentJob() {
        return patientCurrentJob;
    }

    public void setPatientCurrentJob(String patientCurrentJob) {
        this.patientCurrentJob = patientCurrentJob;
    }
}
