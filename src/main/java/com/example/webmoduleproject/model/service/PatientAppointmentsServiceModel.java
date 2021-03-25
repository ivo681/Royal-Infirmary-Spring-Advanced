package com.example.webmoduleproject.model.service;

import com.example.webmoduleproject.model.view.commonDetails.AppointmentDetails;

public class PatientAppointmentsServiceModel extends AppointmentDetails {
    private String doctor;
    private String job;
    private Long hospitalId;

    public PatientAppointmentsServiceModel() {
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

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
}
