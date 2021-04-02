package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.view.prescriptions.PrescriptionListAllViewModel;
import com.example.webmoduleproject.model.view.prescriptions.PrescriptionViewModel;

import java.util.List;

public interface PrescriptionService {

    boolean existingPrescriptionByAppointmentId(String appointmentId);

    void createNewPrescription(String appointmentId, String medicines);

    PrescriptionViewModel getPrescriptionByAppointmentId(String appointmentId);

    List<PrescriptionListAllViewModel> getAllPrescriptionsByMdEmail(String userEmail);

    List<PrescriptionListAllViewModel> getAllPrescriptionsByPatientEmail(String userEmail);

    void createPrescriptionFromSeededAppointments(String id, String medicines);
}
