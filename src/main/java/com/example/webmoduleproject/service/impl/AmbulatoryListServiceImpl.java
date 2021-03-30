package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.AmbulatoryList;
import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.service.documents.AmbulatoryListServiceModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListAllViewModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListViewModel;
import com.example.webmoduleproject.repository.AmbulatoryListRepository;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AmbulatoryListServiceImpl implements AmbulatoryListService {
    private final AmbulatoryListRepository ambulatoryListRepository;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    public AmbulatoryListServiceImpl(AmbulatoryListRepository ambulatoryListRepository, AppointmentService appointmentService, ModelMapper modelMapper) {
        this.ambulatoryListRepository = ambulatoryListRepository;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedLists(String appointmentId, String medicines, String diagnosis) {
        Appointment appointmentById = this.appointmentService.getAppointmentById(appointmentId);
        AmbulatoryList ambulatoryList = new AmbulatoryList();
        ambulatoryList.setDate(appointmentById.getDate());
        ambulatoryList.setMd(appointmentById.getMd());
        ambulatoryList.setPatient(appointmentById.getPatient());
        ambulatoryList.setNumber(generateDocumentNumber());
        ambulatoryList.setPatientTelephoneNumber(appointmentById.getPatient().getTelephone());
        ambulatoryList.setMdTelephoneNumber(appointmentById.getMd().getTelephone());
        ambulatoryList.setPatientHomeAddress(appointmentById.getPatient().getAddress());
        ambulatoryList.setMedicines(medicines);
        ambulatoryList.setDiagnosis(diagnosis);
        ambulatoryList.setAppointment(appointmentById);
        this.ambulatoryListRepository.save(ambulatoryList);
    }

    @Override
    public void createNewList(String appointmentId, AmbulatoryListServiceModel ambulatoryListServiceModel) {
        AmbulatoryList ambulatoryList = this.modelMapper.map(ambulatoryListServiceModel, AmbulatoryList.class);
        ambulatoryList.setDate(LocalDate.now());
        ambulatoryList.setMd(this.appointmentService.getMdByAppointmentId(appointmentId));
        ambulatoryList.setPatient(this.appointmentService.getPatientByAppointmentId(appointmentId));
        ambulatoryList.setNumber(generateDocumentNumber());
        ambulatoryList.setPatientTelephoneNumber(ambulatoryList.getPatient().getTelephone());
        ambulatoryList.setMdTelephoneNumber(ambulatoryList.getMd().getTelephone());
        ambulatoryList.setPatientHomeAddress(this.appointmentService.getPatientByAppointmentId(appointmentId).getAddress());
        ambulatoryList.setMedicines(ambulatoryListServiceModel.getMedicines().trim());
        ambulatoryList.setAppointment(this.appointmentService.getAppointmentById(appointmentId));
        this.ambulatoryListRepository.save(ambulatoryList);
    }

    @Override
    public boolean existingListForAppointment(String appointmentId) {
        return this.ambulatoryListRepository.findByAppointmentId(appointmentId).isPresent();
    }

    @Override
    public AmbulatoryListViewModel getAmbulatoryListByAppointmentId(String appointmentId) {
        AmbulatoryListServiceModel serviceModel = this.modelMapper.map(this.ambulatoryListRepository.findByAppointmentId(appointmentId).get(),
                AmbulatoryListServiceModel.class);
        return this.modelMapper.map(serviceModel, AmbulatoryListViewModel.class);
    }

    @Override
    public List<AmbulatoryListAllViewModel> getAllListsByMdName(String userEmail) {
        return this.ambulatoryListRepository.findAllByMd_EmailOrderByDateDesc(userEmail).stream().map(
                list -> {
                    AmbulatoryListAllViewModel allViewModel = this.modelMapper.map(list, AmbulatoryListAllViewModel.class);
                    allViewModel.setAppointmentId(list.getAppointment().getId());
                    allViewModel.setFirstName(list.getPatient().getFirstName());
                    allViewModel.setLastName(list.getPatient().getLastName());
                    allViewModel.setIdNumber(list.getPatient().getIdNumber());
                    return allViewModel;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<AmbulatoryListAllViewModel> getAllListsByPatientEmail(String userEmail) {
        return this.ambulatoryListRepository.findAllByPatient_EmailOrderByDateDesc(userEmail).stream().map(
                list -> {
                    AmbulatoryListAllViewModel allViewModel = this.modelMapper.map(list, AmbulatoryListAllViewModel.class);
                    allViewModel.setAppointmentId(list.getAppointment().getId());
                    allViewModel.setFirstName(list.getMd().getFirstName());
                    allViewModel.setLastName(list.getMd().getLastName());
                    allViewModel.setJob(list.getMd().getJob());
                    allViewModel.setIdNumber(String.valueOf(list.getMd().getHospitalId()));
                    return allViewModel;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public String getDiagnosisFromListByAppointmentId(String appointmentId) {
        return this.ambulatoryListRepository.findDiagnosisByAppointmentId(appointmentId).get();
    }

    private Long generateDocumentNumber(){
        Random random = new Random();
        long documentNumber = (long) (100000000 + random.nextInt(900000000));
        while (this.ambulatoryListRepository.findByNumber(documentNumber).isPresent()){
            documentNumber = (long) (100000000 + random.nextInt(900000000));
        }
        return documentNumber;
    }
}
