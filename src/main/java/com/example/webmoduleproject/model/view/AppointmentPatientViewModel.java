package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.AppointmentDetails;

import java.time.LocalDate;

public class AppointmentPatientViewModel extends AppointmentDetails {
    private String doctor;
    private String job;
    private String hospitalId;

    public AppointmentPatientViewModel() {
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

}
