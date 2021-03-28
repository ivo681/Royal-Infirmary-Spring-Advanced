package com.example.webmoduleproject.model.view.buildBlocks;

import com.example.webmoduleproject.model.view.commonDetails.UserBasicDetails;

public class PatientSickLeaveDetails extends UserBasicDetails {
    private String job;
    private String idNumber;
    private String employer;
    private String diagnosis;

    public PatientSickLeaveDetails() {
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
