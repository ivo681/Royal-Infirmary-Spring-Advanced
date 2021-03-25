package com.example.webmoduleproject.model.view;

import com.example.webmoduleproject.model.view.commonDetails.AppointmentDetails;

public class AppointmentDoctorViewModel extends AppointmentDetails {
    private String firstName;
    private String lastName;
    private String idNumber;

    public AppointmentDoctorViewModel() {
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
