package com.example.webmoduleproject.model.entities;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
public class SickLeave extends BaseDocumentEntity {
    private LocalDate fromDate;
    private LocalDate toDate;
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

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

}
