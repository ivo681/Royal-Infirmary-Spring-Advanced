package com.example.webmoduleproject.model.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class LogEntity extends BaseEntity{
    private User user;
    private String action;
    private LocalDateTime dateTime;

    public LogEntity() {
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "action", nullable = false)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name = "date_time", nullable = false)
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
