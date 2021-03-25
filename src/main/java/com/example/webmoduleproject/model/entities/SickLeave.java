package com.example.webmoduleproject.model.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
public class SickLeave extends BaseEntity {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long number;
    private User md;
    private User patient;
    private String mdTelephoneNumber;
    private String patientTelephoneNumber;
    private String patientHomeAddress;
    private String patientEmployer;
    private String patientJob;
    private String diagnosis;
    private String reason;
    private Appointment appointment;

    public SickLeave() {
    }

    @Column(name = "from_date", nullable = false)
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @Column(name = "to_date", nullable = false)
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    @Column(name = "number", nullable = false, unique = true)
    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    @ManyToOne
    public User getMd() {
        return md;
    }

    public void setMd(User md) {
        this.md = md;
    }

    @ManyToOne
    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    @Column(name = "patient_telephone_number", nullable = false)
    public String getMdTelephoneNumber() {
        return mdTelephoneNumber;
    }

    public void setMdTelephoneNumber(String mdTelephoneNumber) {
        this.mdTelephoneNumber = mdTelephoneNumber;
    }

    @Column(name = "patient_address", nullable = false)
    public String getPatientHomeAddress() {
        return patientHomeAddress;
    }

    public void setPatientHomeAddress(String patientHomeAddress) {
        this.patientHomeAddress = patientHomeAddress;
    }

    @Column(name = "patient_employer", nullable = false)
    public String getPatientEmployer() {
        return patientEmployer;
    }

    public void setPatientEmployer(String patientEmployer) {
        this.patientEmployer = patientEmployer;
    }

    @Column(name = "patient_job", nullable = false)
    public String getPatientJob() {
        return patientJob;
    }

    public void setPatientJob(String patientJob) {
        this.patientJob = patientJob;
    }

    @Column(name = "diagnosis", nullable = false)
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Column(name = "reason", nullable = false)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @OneToOne
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Column(name = "patient_number", nullable = false)
    public String getPatientTelephoneNumber() {
        return patientTelephoneNumber;
    }

    public void setPatientTelephoneNumber(String patientTelephoneNumber) {
        this.patientTelephoneNumber = patientTelephoneNumber;
    }
}
