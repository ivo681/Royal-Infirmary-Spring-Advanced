package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.binding.AmbulatoryListBindingModel;
import com.example.webmoduleproject.model.view.AmbulatoryListAllViewModel;
import com.example.webmoduleproject.model.view.AmbulatoryListViewModel;

import java.util.List;

public interface AmbulatoryListService {

    Long getAmbulatoryListNumber();

    void createNewList(String appointmentId, AmbulatoryListBindingModel ambulatoryListBindingModel);

    boolean existingListForAppointment(String appointmentId);

    AmbulatoryListViewModel getAmbulatoryListByAppointmentId(String appointmentId);

    List<AmbulatoryListAllViewModel> getAllListsByMdName(String userEmail);

    List<AmbulatoryListAllViewModel> getAllListsByPatientEmail(String userEmail);

    String getDiagnosisFromListByAppointmentId(String appointmentId);
}
