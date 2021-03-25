package com.example.webmoduleproject.model.service;

import com.example.webmoduleproject.model.view.commonDetails.AppointmentDetails;

public class DoctorAppointmentsServiceModel extends AppointmentDetails {
    private String firstName;
    private String lastName;
    private String idNumber;

    public DoctorAppointmentsServiceModel() {
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
