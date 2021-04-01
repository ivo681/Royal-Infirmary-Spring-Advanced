package com.example.webmoduleproject.model.view.commonDetails;

public abstract class DocumentDetails {
    private Long number;
    private String mdTelephoneNumber;
    private String patientTelephoneNumber;
    private String patientHomeAddress;
    private String diagnosis;

    public DocumentDetails() {
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getMdTelephoneNumber() {
        return mdTelephoneNumber;
    }

    public void setMdTelephoneNumber(String mdTelephoneNumber) {
        this.mdTelephoneNumber = mdTelephoneNumber;
    }

    public String getPatientHomeAddress() {
        return patientHomeAddress;
    }

    public void setPatientHomeAddress(String patientHomeAddress) {
        this.patientHomeAddress = patientHomeAddress;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPatientTelephoneNumber() {
        return patientTelephoneNumber;
    }

    public void setPatientTelephoneNumber(String patientTelephoneNumber) {
        this.patientTelephoneNumber = patientTelephoneNumber;
    }
}
