package com.example.webmoduleproject.model.binding;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class SickLeaveBindingModel {
    private String reason;
    private LocalDate fromDate;
    private LocalDate toDate;

    public SickLeaveBindingModel() {
    }

    @NotBlank(message = "Please provide a reason for the work absence")
    @Length(min = 3, message = "Reason must be at least 3 characters")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @NotNull(message = "Please select an end date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "End date cannot be in the past or today")
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
