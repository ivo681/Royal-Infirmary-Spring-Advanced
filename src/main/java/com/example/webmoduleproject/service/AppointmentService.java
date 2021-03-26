package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.service.AppointmentServiceModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientAmbulatoryListDetails;
import com.example.webmoduleproject.model.view.appointments.AppointmentConfirmViewModel;
import com.example.webmoduleproject.model.view.appointments.DoctorAppointmentViewModel;
import com.example.webmoduleproject.model.view.appointments.PatientAppointmentViewModel;
import com.example.webmoduleproject.model.view.buildBlocks.PatientPrescriptionDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientSickLeaveDetails;

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

    List<DoctorAppointmentViewModel> getTodaysAppointmentsForDoctor(String userEmail);

    List<DoctorAppointmentViewModel> getPastAppointmentsForDoctor(String userEmail);

    List<DoctorAppointmentViewModel> getFutureAppointmentsForDoctor(String userEmail);

    List<PatientAppointmentViewModel> getPastAppointmentsForPatient(String userEmail);

    List<PatientAppointmentViewModel> getFutureAppointmentsForPatient(String userEmail);

    PatientAmbulatoryListDetails getPatientViewModelByAppointmentId(String id);

    MdDocumentDetails getMdDetailsByAppointmentId(String id);

    User getMdByAppointmentId(String id);

    User getPatientByAppointmentId(String id);

    Appointment getAppointmentById(String appointmentId);

    PatientPrescriptionDetails getPatientPrescriptionViewModelByAppointmentId(String appointmentId);

    PatientSickLeaveDetails getSickPatientViewModelByAppointmentId(String appointmentId);

    void deleteUnconfirmedAndUnattendedAppointmentsFromDatabase();

    void changeStatusOfCompletedAppointmentsForTheDay();

    void updateStatusofNoShowPatientsForAppointments();
}
