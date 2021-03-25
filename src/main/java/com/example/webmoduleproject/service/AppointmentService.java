package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.service.AppointmentServiceModel;
import com.example.webmoduleproject.model.view.*;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    boolean doesAppointmentExistById(String id);

    String appointmentCreate(AppointmentServiceModel model);

    AppointmentConfirmViewModel getUnconfirmedAppointmentById(String id);

    boolean isAppointmentUnconfirmed(String id);

    void cancelAppointmentById(String id);

    void confirmAppointmentById(String id);

    boolean checkAvailabilityForDateAndTime(LocalDate date, String timeSpan, String mdName);

    boolean checkForConfirmedAppointment(LocalDate date, String userEmail, String mdId);

    List<AppointmentDoctorViewModel> getTodaysAppointmentsForDoctor(String userEmail);

    List<AppointmentDoctorViewModel> getPastAppointmentsForDoctor(String userEmail);

    List<AppointmentDoctorViewModel> getFutureAppointmentsForDoctor(String userEmail);

    List<AppointmentPatientViewModel> getPastAppointmentsForPatient(String userEmail);

    List<AppointmentPatientViewModel> getFutureAppointmentsForPatient(String userEmail);

    MdDocumentViewModel getMdViewModelByAppointmentId(String id);

    PatientAmbulatoryListViewModel getPatientViewModelByAppointmentId(String id);

    MdDocumentViewModel getMdDetailsByAppointmentId(String id);

    PatientAmbulatoryListViewModel getPatientDetailsByAppointmentId(String id);

    User getMdByAppointmentId(String id);

    User getPatientByAppointmentId(String id);

    Appointment getAppointmentById(String appointmentId);

    PatientPrescriptionViewModel getPatientPrescriptionViewModelByAppointmentId(String appointmentId);

    PatientSickLeaveViewModel getSickPatientViewModelByAppointmentId(String appointmentId);

    void deleteUnconfirmedAndUnattendedAppointmentsFromDatabase();

    void changeStatusOfCompletedAppointmentsForTheDay();

    void updateStatusofNoShowPatientsForAppointments();
}
