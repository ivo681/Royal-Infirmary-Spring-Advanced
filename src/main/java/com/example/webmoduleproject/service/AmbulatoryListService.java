package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.service.documents.AmbulatoryListServiceModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListAllViewModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListViewModel;

import java.util.List;

public interface AmbulatoryListService {

    void createNewList(String appointmentId, AmbulatoryListServiceModel ambulatoryListBindingModel);

    boolean existingListForAppointment(String appointmentId);

    AmbulatoryListViewModel getAmbulatoryListByAppointmentId(String appointmentId);

    List<AmbulatoryListAllViewModel> getAllListsByMdName(String userEmail);

    List<AmbulatoryListAllViewModel> getAllListsByPatientEmail(String userEmail);

    String getDiagnosisFromListByAppointmentId(String appointmentId);

    void seedLists(String appointmentId, String medicines, String diagnosis);
}
