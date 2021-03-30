package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.Appointment;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SickLeaveServiceImpl implements SickLeaveService {
    private final SickLeaveRepository sickLeaveRepository;
    private final AmbulatoryListService ambulatoryListService;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;
    private final Random random;

    public SickLeaveServiceImpl(SickLeaveRepository sickLeaveRepository, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, ModelMapper modelMapper, Random random) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
        this.random = random;
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
        sickLeaveServiceModel.setPatientCurrentEmployer(sickLeave.getPatientEmployer());
        sickLeaveServiceModel.setPatientCurrentJob(sickLeave.getPatientJob());
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

    @Override
    public void createSickLeaveFromSeededAppointments(String appointmentId, String diagnosis) {
        SickLeave sickLeave = new SickLeave();
        sickLeave.setNumber(generateDocumentNumber());
        Appointment appointmentById = this.appointmentService.getAppointmentById(appointmentId);
        sickLeave.setMd(appointmentById.getMd());
        sickLeave.setPatient(appointmentById.getPatient());
        sickLeave.setAppointment(appointmentById);
        sickLeave.setMdTelephoneNumber(appointmentById.getMd().getTelephone());
        sickLeave.setPatientTelephoneNumber(appointmentById.getPatient().getTelephone());
        sickLeave.setPatientEmployer(appointmentById.getPatient().getEmployer());
        sickLeave.setPatientJob(appointmentById.getPatient().getJob());
        sickLeave.setPatientHomeAddress(appointmentById.getPatient().getAddress());
        sickLeave.setDiagnosis(diagnosis);
        sickLeave.setFromDate(appointmentById.getDate());
        sickLeave.setToDate(appointmentById.getDate().plusDays(random.nextInt(10) + 1));
        sickLeave.setReason("Unable to work due to " + diagnosis);
        this.sickLeaveRepository.save(sickLeave);
    }

    private Long generateDocumentNumber(){
        long documentNumber = (long) (100000000 + random.nextInt(900000000));
        while (this.sickLeaveRepository.findByNumber(documentNumber).isPresent()){
            documentNumber = (long) (100000000 + random.nextInt(900000000));
        }
        return documentNumber;
    }
}
