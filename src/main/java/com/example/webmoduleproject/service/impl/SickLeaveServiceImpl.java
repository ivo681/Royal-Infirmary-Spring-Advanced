package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.SickLeave;
import com.example.webmoduleproject.model.service.documents.SickLeaveBindingServiceModel;
import com.example.webmoduleproject.model.service.documents.SickLeaveListAllServiceModel;
import com.example.webmoduleproject.model.service.documents.SickLeaveServiceModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveViewModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveListAllViewModel;
import com.example.webmoduleproject.repository.SickLeaveRepository;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.SickLeaveService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SickLeaveServiceImpl implements SickLeaveService {
    private final SickLeaveRepository sickLeaveRepository;
    private final AmbulatoryListService ambulatoryListService;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    public SickLeaveServiceImpl(SickLeaveRepository sickLeaveRepository, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, ModelMapper modelMapper) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean existingSickLeaveByAppointmentIdMdCheck(String appointmentId, String userEmail) {
        return this.sickLeaveRepository.getSickLeaveByAppointmentIdAndMdEmail(appointmentId, userEmail).isPresent();
    }

    @Override
    public void createNewSickLeave(String appointmentId, SickLeaveBindingServiceModel sickLeaveBindingServiceModel) {
        SickLeave sickLeave = this.modelMapper.map(sickLeaveBindingServiceModel, SickLeave.class);
        sickLeave.setNumber(generateDocumentNumber());
        sickLeave.setMd(this.appointmentService.getMdByAppointmentId(appointmentId));
        sickLeave.setPatient(this.appointmentService.getPatientByAppointmentId(appointmentId));
        sickLeave.setAppointment(this.appointmentService.getAppointmentById(appointmentId));
        sickLeave.setMdTelephoneNumber(sickLeave.getMd().getTelephone());
        sickLeave.setPatientTelephoneNumber(sickLeave.getPatient().getTelephone());
        sickLeave.setPatientEmployer(sickLeave.getPatient().getEmployer());
        sickLeave.setPatientJob(sickLeave.getPatient().getJob());
        sickLeave.setPatientHomeAddress(sickLeave.getPatient().getAddress());
        sickLeave.setDiagnosis(this.ambulatoryListService.getDiagnosisFromListByAppointmentId(appointmentId));
        this.sickLeaveRepository.save(sickLeave);
    }

    @Override
    public boolean existingSickLeaveByAppointmentId(String appointmentId) {
        return this.sickLeaveRepository.getSickLeaveByAppointmentId(appointmentId).isPresent();
    }

    @Override
    public SickLeaveViewModel getSickLeaveByAppointmentId(String appointmentId) {
        SickLeave sickLeave = this.sickLeaveRepository.getSickLeaveByAppointmentId(appointmentId).get();
        SickLeaveServiceModel sickLeaveServiceModel = this.modelMapper.map(sickLeave, SickLeaveServiceModel.class);
        sickLeaveServiceModel.setPatientEmployer(sickLeave.getPatientEmployer());
        sickLeaveServiceModel.setPatientJob(sickLeave.getPatientJob());
        sickLeaveServiceModel.setPatientTelephoneNumber(sickLeave.getPatientTelephoneNumber());
        sickLeaveServiceModel.setPatientAddress(sickLeave.getPatientHomeAddress());
        return this.modelMapper.map(sickLeaveServiceModel, SickLeaveViewModel.class);
    }

    @Override
    public List<SickLeaveListAllViewModel> getAllSickLeavesByMdName(String userEmail) {
        List<SickLeaveListAllServiceModel> serviceModels = this.sickLeaveRepository.getAllByMdEmail(userEmail).stream().map(sickLeave -> {
            SickLeaveListAllServiceModel model = this.modelMapper.map(sickLeave, SickLeaveListAllServiceModel.class);
            model.setFirstName(sickLeave.getPatient().getFirstName());
            model.setLastName(sickLeave.getPatient().getLastName());
            model.setJob(sickLeave.getPatientJob());
            model.setIdNumber(sickLeave.getPatient().getIdNumber());
            model.setAppointmentId(sickLeave.getAppointment().getId());
            return model;
        }).collect(Collectors.toList());

        return serviceModels.stream().map(model -> this.modelMapper.map(model,
                SickLeaveListAllViewModel.class)).collect(Collectors.toList());
    }

    @Override
    public List<SickLeaveListAllViewModel> getOwnSickLeaves(String userEmail) {
        List<SickLeaveListAllServiceModel> serviceModels = this.sickLeaveRepository.getAllByPatientEmail(userEmail).stream().map(sickLeave -> {
            SickLeaveListAllServiceModel model = this.modelMapper.map(sickLeave, SickLeaveListAllServiceModel.class);
            model.setFirstName(sickLeave.getMd().getFirstName());
            model.setLastName(sickLeave.getMd().getLastName());
            model.setJob(sickLeave.getMd().getJob());
            model.setIdNumber(String.valueOf(sickLeave.getMd().getHospitalId()));
            model.setAppointmentId(sickLeave.getAppointment().getId());
            return model;
        }).collect(Collectors.toList());

        return serviceModels.stream().map(model -> this.modelMapper.map(model,
                SickLeaveListAllViewModel.class)).collect(Collectors.toList());
    }

    private Long generateDocumentNumber(){
        Random random = new Random();
        long documentNumber = (long) (100000000 + random.nextInt(900000000));
        while (this.sickLeaveRepository.findByNumber(documentNumber).isPresent()){
            documentNumber = (long) (100000000 + random.nextInt(900000000));
        }
        return documentNumber;
    }
}
