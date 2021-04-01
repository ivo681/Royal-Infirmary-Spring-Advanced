package com.example.webmoduleproject.model.service;

import com.example.webmoduleproject.model.entities.User;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class LogServiceModel {
    private String userEmail;
    private String action;
    private LocalDateTime dateTime;

    public LogServiceModel() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
