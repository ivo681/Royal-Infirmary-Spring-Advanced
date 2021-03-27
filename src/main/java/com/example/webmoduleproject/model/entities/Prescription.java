package com.example.webmoduleproject.model.entities;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
public class Prescription extends BaseEntity{
    private LocalDate date;
    private Long number;
    private User md;
    private User patient;
    private String mdTelephoneNumber;
    private String patientHomeAddress;
    private String medicines;
    private Appointment appointment;

    public Prescription() {
    }

    @Column(name = "date", nullable = false)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    @Column(name = "patient_address", nullable = false)
    public String getPatientHomeAddress() {
        return patientHomeAddress;
    }

    public void setPatientHomeAddress(String patientHomeAddress) {
        this.patientHomeAddress = patientHomeAddress;
    }

    @Column(name = "medicines_prescribed", nullable = false, columnDefinition = "TEXT")
    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    @Column(name = "number", nullable = false, unique = true)
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

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

}
