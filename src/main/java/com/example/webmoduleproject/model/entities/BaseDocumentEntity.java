package com.example.webmoduleproject.model.entities;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@MappedSuperclass
public abstract class BaseDocumentEntity extends BaseEntity{
    private Long number;
    private User md;
    private User patient;
    private String mdTelephoneNumber;
    private String patientTelephoneNumber;
    private String patientHomeAddress;
    private Appointment appointment;

    public BaseDocumentEntity(){

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

    @Column(name = "patient_tel_number", nullable = false)
    public String getPatientTelephoneNumber() {
        return patientTelephoneNumber;
    }

    public void setPatientTelephoneNumber(String patientTelephoneNumber) {
        this.patientTelephoneNumber = patientTelephoneNumber;
    }

    @Column(name = "md_tel_number")
    public String getMdTelephoneNumber() {
        return mdTelephoneNumber;
    }

    public void setMdTelephoneNumber(String mdTelephoneNumber) {
        this.mdTelephoneNumber = mdTelephoneNumber;
    }

    @OneToOne
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Column(name = "patient_address", nullable = false)
    public String getPatientHomeAddress() {
        return patientHomeAddress;
    }

    public void setPatientHomeAddress(String patientHomeAddress) {
        this.patientHomeAddress = patientHomeAddress;
    }


}
