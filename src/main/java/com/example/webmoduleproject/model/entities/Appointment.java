package com.example.webmoduleproject.model.entities;

import com.example.webmoduleproject.model.entities.enums.StatusEnum;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity{
    private User patient;
    private User md;
    private LocalDate date;
    private String reason;
    private String timeSpan;
    private StatusEnum status;

    public Appointment() {
    }

    @ManyToOne
    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    @ManyToOne
    public User getMd() {
        return md;
    }

    public void setMd(User md) {
        this.md = md;
    }

    @Column(name = "date", nullable = false)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Column(name = "reason", nullable = false)
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "time_span", nullable = false)
    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}
