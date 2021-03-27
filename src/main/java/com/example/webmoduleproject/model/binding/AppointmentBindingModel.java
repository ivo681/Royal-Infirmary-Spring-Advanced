package com.example.webmoduleproject.model.binding;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class AppointmentBindingModel {
    private String userEmail;
    private String md_Id;
    private LocalDate date;
    private String reason;
    private String timeSpan;

    public AppointmentBindingModel() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMd_Id() {
        return md_Id;
    }

    public void setMd_Id(String mdId) {
        this.md_Id = mdId;
    }

    @NotNull(message = "Please select a date for your appointment")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Appointment date cannot be in the past or today")
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @NotBlank(message = "Please provide a reason for your appointment")
    @Length(min = 3, message = "Reason must be at least 3 characters")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @NotBlank(message = "You must select a time span to visit")
    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

}
