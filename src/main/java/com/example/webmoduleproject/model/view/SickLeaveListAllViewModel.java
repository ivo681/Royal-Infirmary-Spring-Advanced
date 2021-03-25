package com.example.webmoduleproject.model.view;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SickLeaveListAllViewModel {
    private String appointmentId;
    private String firstName;
    private String lastName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String job;
    private String idNumber;

    public SickLeaveListAllViewModel() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
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
}
