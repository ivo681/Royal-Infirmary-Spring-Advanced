package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.dtos.AppointmentSeedDto;
import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.model.service.*;
import com.example.webmoduleproject.model.service.documents.PatientPrescriptionDetailsServiceModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientAmbulatoryListDetails;
import com.example.webmoduleproject.model.view.appointments.AppointmentConfirmViewModel;
import com.example.webmoduleproject.model.view.appointments.DoctorAppointmentViewModel;
import com.example.webmoduleproject.model.view.appointments.PatientAppointmentViewModel;
import com.example.webmoduleproject.model.view.buildBlocks.PatientPrescriptionDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientSickLeaveDetails;
import com.example.webmoduleproject.repository.AppointmentRepository;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.service.*;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String APPOINTMENTS_PATH = "src/main/resources/static/json/appointments.json";
    private static Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final UserRepository userRepository;
    private final UserService userService;
    private final AppointmentRepository appointmentRepository;
    private final AmbulatoryListService ambulatoryListService;
    private final PrescriptionService prescriptionService;
    private final SickLeaveService sickLeaveService;
    private final ModelMapper modelMapper;
    private final Gson gson;

    public AppointmentServiceImpl(UserRepository userRepository, UserService userService,
                                  AppointmentRepository appointmentRepository,
                                  @NonNull @Lazy AmbulatoryListService ambulatoryListService,
                                  @NonNull @Lazy PrescriptionService prescriptionService,
                                  @NonNull @Lazy SickLeaveService sickLeaveService,
                                  ModelMapper modelMapper, Gson gson) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
        this.ambulatoryListService = ambulatoryListService;
        this.prescriptionService = prescriptionService;
        this.sickLeaveService = sickLeaveService;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean doesAppointmentExistByIdAndMdEmail(String id, String userEmail) {
        return this.appointmentRepository.findByIdAndMd_Email(id, userEmail).isPresent();
    }

    @Override
    public boolean doesAppointmentExistByIdAndPatientEmail(String id, String userEmail) {
        return this.appointmentRepository.findByIdAndPatient_Email(id, userEmail).isPresent();
    }

    @Override
    public String appointmentCreate(AppointmentServiceModel model) {
        Appointment appointment = this.modelMapper.map(model, Appointment.class);
        appointment.setMd(this.userRepository.findById(model.getMd_Id()).orElse(null));
        appointment.setPatient(this.userRepository.findByEmail(model.getUserEmail()).orElse(null));
        appointment.setStatus(StatusEnum.UNCONFIRMED);
        Appointment saved = this.appointmentRepository.saveAndFlush(appointment);
        return saved.getId();
    }

    @Override
    public AppointmentConfirmViewModel getUnconfirmedAppointmentById(String id) {
        Appointment unconfirmedEntity = this.appointmentRepository.findById(id).get();
        AppointmentConfirmViewModel confirmViewModel = this.modelMapper.map(unconfirmedEntity, AppointmentConfirmViewModel.class);
        confirmViewModel.setMd_id(unconfirmedEntity.getMd().getId());
        confirmViewModel.setMdFullName(this.userService.getMdFullNameById(confirmViewModel.getMd_id()));
        return confirmViewModel;
    }

    @Override
    public boolean isAppointmentUnconfirmed(String id) {
        return this.appointmentRepository.
                findUnconfirmedAppointmentById(id, StatusEnum.UNCONFIRMED).isPresent();
    }

    @Override
    public void cancelAppointmentById(String id) {
        this.appointmentRepository.deleteById(id);
    }

    @Override
    public void confirmAppointmentById(String id) {
        Appointment unconfirmedAppointment = this.appointmentRepository.findById(id).get();
        unconfirmedAppointment.setStatus(StatusEnum.CONFIRMED);
        this.appointmentRepository.save(unconfirmedAppointment);
    }

    @Override
    public boolean checkAvailabilityForDateAndTime(LocalDate date, String timeSpan, String mdName) {
        return this.appointmentRepository.
                findAllAppointmentsByDateAndTime(date, timeSpan, mdName, StatusEnum.CONFIRMED) < 4;
    }

    @Override
    public boolean checkForConfirmedAppointment(LocalDate date, String userEmail, String mdId) {
        return this.appointmentRepository.findConfirmedAppointmentForUser(date, userEmail, mdId, StatusEnum.CONFIRMED).isPresent();
    }

    @Override
    public List<DoctorAppointmentViewModel> getTodaysAppointmentsForDoctor(String userEmail) {
        List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getTodaysAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(this::convertDoctorAppointmentServiceModelToView).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorAppointmentViewModel> getPastAppointmentsForDoctor(String userEmail) {
        List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getPastAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CLOSED).stream().map(this::convertDoctorAppointmentServiceModelToView).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorAppointmentViewModel> getFutureAppointmentsForDoctor(String userEmail) {
       List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getFutureAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(this::convertDoctorAppointmentServiceModelToView).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    private DoctorAppointmentsServiceModel convertDoctorAppointmentServiceModelToView(Appointment appointment) {
        DoctorAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, DoctorAppointmentsServiceModel.class);
        serviceModel.setFirstName(appointment.getPatient().getFirstName());
        serviceModel.setLastName(appointment.getPatient().getLastName());
        serviceModel.setIdNumber(appointment.getPatient().getIdNumber());
        return serviceModel;
    }

    @Override
    public List<PatientAppointmentViewModel> getPastAppointmentsForPatient(String userEmail) {
        List<PatientAppointmentsServiceModel> patientAppointmentsServiceModels = this.appointmentRepository.getPastAppointmentsForPatient(userEmail,
                LocalDate.now(), StatusEnum.CLOSED).stream().map(this::convertPatientAppointmentServiceModelToView).collect(Collectors.toList());
        return patientAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, PatientAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientAppointmentViewModel> getFutureAppointmentsForPatient(String userEmail) {
        List<PatientAppointmentsServiceModel> patientAppointmentsServiceModels = this.appointmentRepository.getFutureAppointmentsForPatient(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(this::convertPatientAppointmentServiceModelToView).collect(Collectors.toList());
        return patientAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, PatientAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    private PatientAppointmentsServiceModel convertPatientAppointmentServiceModelToView(Appointment appointment) {
        PatientAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, PatientAppointmentsServiceModel.class);
        serviceModel.setDoctor(String.format("%s %s", appointment.getMd().getFirstName(), appointment.getMd().getLastName()));
        serviceModel.setJob(appointment.getMd().getJob());
        serviceModel.setHospitalId(appointment.getMd().getHospitalId());
        return serviceModel;
    }

    @Override
    public PatientAmbulatoryListDetails getPatientViewModelByAppointmentId(String id) {
        return this.modelMapper.map(
                this.appointmentRepository.getPatientByPatientId(id).get(),
                PatientAmbulatoryListDetails.class);
    }

    @Override
    public MdDocumentDetails getMdDetailsByAppointmentId(String id) {
        MdDocumentDetailsServiceModel serviceModel = this.modelMapper.map(
                this.appointmentRepository.getMdByMdId(id).get(),
                MdDocumentDetailsServiceModel.class);
        return this.modelMapper.map(serviceModel, MdDocumentDetails.class);
    }

    @Override
    public User getMdByAppointmentId(String id) {
        return this.appointmentRepository.getMdByMdId(id).get();
    }

    @Override
    public User getPatientByAppointmentId(String id) {
        return this.appointmentRepository.getPatientByPatientId(id).get();
    }

    @Override
    public Appointment getAppointmentById(String appointmentId) {
        return this.appointmentRepository.findById(appointmentId).get();
    }

    @Override
    public PatientPrescriptionDetails getPatientPrescriptionViewModelByAppointmentId(String appointmentId) {
        PatientPrescriptionDetailsServiceModel serviceModel = this.modelMapper.map(this.appointmentRepository.
                        getPatientByPatientId(appointmentId).get(),
                PatientPrescriptionDetailsServiceModel.class);
        return this.modelMapper.map(serviceModel, PatientPrescriptionDetails.class);
    }

    @Override
    public PatientSickLeaveDetails getSickPatientViewModelByAppointmentId(String appointmentId) {
        return this.modelMapper.map(this.appointmentRepository.
                        getPatientByPatientId(appointmentId).get(),
                PatientSickLeaveDetails.class);
    }

    @Override
    public void deleteUnconfirmedAndUnattendedAppointmentsFromDatabase() {
        List<Appointment> unconfirmedAndUnattendedAppointmentsFromDataBase = this.appointmentRepository.
                getUnconfirmedAndUnattendedAppointmentsFromDataBase(StatusEnum.UNCONFIRMED, StatusEnum.NO_SHOW);
        this.appointmentRepository.deleteAll(unconfirmedAndUnattendedAppointmentsFromDataBase);
    }

    @Override
    public void changeStatusOfCompletedAppointmentsForTheDay() {
        this.appointmentRepository.updateTodaysConfirmedAppointmentStatus(
                StatusEnum.CONFIRMED, StatusEnum.CLOSED, LocalDate.now()
        );
    }

    @Override
    public void updateStatusofNoShowPatientsForAppointments() {
        this.appointmentRepository.updateStatusOfNoShows(
                StatusEnum.CONFIRMED, StatusEnum.NO_SHOW, LocalDate.now());
    }

    @Async
    @Override
    public void seedAppointments() throws IOException {
        if (this.appointmentRepository.count() == 0){
            LOGGER.info("Starting to seed appointments");
            String content = String.join("", Files.readAllLines(Path.of(APPOINTMENTS_PATH)));

            AppointmentSeedDto[] appointmentSeedDtos = this.gson.fromJson(content, AppointmentSeedDto[].class);
            for (AppointmentSeedDto appointmentSeedDto : appointmentSeedDtos) {
                Appointment appointment = this.modelMapper.map(appointmentSeedDto, Appointment.class);
                appointment.setMd(this.userRepository.findByEmail(appointmentSeedDto.getMd()).get());
                appointment.setPatient(this.userRepository.findByEmail(appointmentSeedDto.getPatient()).get());
                if (LocalDate.now().compareTo(appointmentSeedDto.getDate()) > 0){
                    appointment.setStatus(StatusEnum.CLOSED);
                    appointment = this.appointmentRepository.save(appointment);
                    this.ambulatoryListService.seedLists(appointment.getId(),
                            appointmentSeedDto.getMedicines(), appointmentSeedDto.getDiagnosis());
                    this.prescriptionService.createPrescriptionFromSeededAppointments(appointment.getId(),
                            appointmentSeedDto.getMedicines());
                    this.sickLeaveService.createSickLeaveFromSeededAppointments(appointment.getId(),
                            appointmentSeedDto.getDiagnosis());
                } else{
                    if (LocalDate.now().compareTo(appointmentSeedDto.getDate()) < 0){
                        appointment.setStatus(StatusEnum.CONFIRMED);
                    } else {
                        appointment.setStatus(StatusEnum.CONFIRMED);
                    }
                    this.appointmentRepository.save(appointment);
                }
            }
            LOGGER.info("End of seeding appointments");
        }

    }

    @Override
    public boolean isUserTheMdInAppointment(String userEmail, String id) {
        return this.appointmentRepository.
                isUserTheMdInAppointment(userEmail, id).isPresent();
    }

    @Override
    public boolean doesUserHaveAccessToDetails(String appointmentId, String userEmail) {
        return this.appointmentRepository.doesUserHaveAccessToAppointment(
                appointmentId, userEmail).isPresent();
    }
}
