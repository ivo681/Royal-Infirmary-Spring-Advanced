package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.binding.SickLeaveBindingModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveViewModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveListAllViewModel;

import java.util.List;

public interface SickLeaveService {
    boolean existingSickLeaveByAppointmentIdMdCheck(String appointmentId, String userEmail);

    void createNewSickLeave(String appointmentId, SickLeaveBindingModel sickLeaveBindingModel);

    boolean existingSickLeaveByAppointmentId(String appointmentId);

    SickLeaveViewModel getSickLeaveByAppointmentId(String appointmentId);

    List<SickLeaveListAllViewModel> getAllSickLeavesByMdName(String userEmail);

    List<SickLeaveListAllViewModel> getOwnSickLeaves(String userEmail);
}
