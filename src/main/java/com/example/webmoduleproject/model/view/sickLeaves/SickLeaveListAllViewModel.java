package com.example.webmoduleproject.model.view.sickLeaves;

import com.example.webmoduleproject.model.view.commonDetails.DocumentListDetails;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SickLeaveListAllViewModel extends DocumentListDetails {
    private LocalDate fromDate;
    private LocalDate toDate;

    public SickLeaveListAllViewModel() {
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

}
