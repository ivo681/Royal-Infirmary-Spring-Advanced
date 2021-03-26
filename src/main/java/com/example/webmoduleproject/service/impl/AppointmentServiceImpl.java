package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.model.service.AppointmentServiceModel;
import com.example.webmoduleproject.model.service.DoctorAppointmentsServiceModel;
import com.example.webmoduleproject.model.service.PatientAppointmentsServiceModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientAmbulatoryListDetails;
import com.example.webmoduleproject.model.view.appointments.AppointmentConfirmViewModel;
import com.example.webmoduleproject.model.view.appointments.DoctorAppointmentViewModel;
import com.example.webmoduleproject.model.view.appointments.PatientAppointmentViewModel;
import com.example.webmoduleproject.model.view.buildBlocks.PatientPrescriptionDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientSickLeaveDetails;
import com.example.webmoduleproject.repository.AppointmentRepository;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    public AppointmentServiceImpl(UserRepository userRepository, UserService userService, AppointmentRepository appointmentRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean doesAppointmentExistById(String id) {
        return this.appointmentRepository.findById(id).isPresent();
    }

    @Override
    public String appointmentCreate(AppointmentServiceModel model) {
        Appointment appointment = this.modelMapper.map(model, Appointment.class);
        appointment.setMd(this.userRepository.findById(model.getMdId()).get());
        appointment.setPatient(this.userRepository.findByEmail(model.getUserEmail()).get());
        appointment.setStatus(StatusEnum.UNCONFIRMED);
        return this.appointmentRepository.save(appointment).getId();
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
                findAllAppointmentsByDateAndTime(date, timeSpan, mdName) < 4;
    }

    @Override
    public boolean checkForConfirmedAppointment(LocalDate date, String userEmail, String mdId) {
        return this.appointmentRepository.findConfirmedAppointmentForUser(date, userEmail, mdId, StatusEnum.CONFIRMED).isPresent();
    }

    @Override
    public List<DoctorAppointmentViewModel> getTodaysAppointmentsForDoctor(String userEmail) {
                List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getTodaysAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(appointment -> {
            DoctorAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, DoctorAppointmentsServiceModel.class);
            serviceModel.setFirstName(appointment.getPatient().getFirstName());
            serviceModel.setLastName(appointment.getPatient().getLastName());
            serviceModel.setIdNumber(appointment.getPatient().getIdNumber());
            return serviceModel;
        }).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorAppointmentViewModel> getPastAppointmentsForDoctor(String userEmail) {
        List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getPastAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CLOSED).stream().map(appointment -> {
            DoctorAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, DoctorAppointmentsServiceModel.class);
            serviceModel.setFirstName(appointment.getPatient().getFirstName());
            serviceModel.setLastName(appointment.getPatient().getLastName());
            serviceModel.setIdNumber(appointment.getPatient().getIdNumber());
            return serviceModel;
        }).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorAppointmentViewModel> getFutureAppointmentsForDoctor(String userEmail) {
        List<DoctorAppointmentsServiceModel> doctorAppointmentsServiceModels = this.appointmentRepository.getFutureAppointmentsForDoctor(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(appointment -> {
            DoctorAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, DoctorAppointmentsServiceModel.class);
            serviceModel.setFirstName(appointment.getPatient().getFirstName());
            serviceModel.setLastName(appointment.getPatient().getLastName());
            serviceModel.setIdNumber(appointment.getPatient().getIdNumber());
            return serviceModel;
        }).collect(Collectors.toList());

        return doctorAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, DoctorAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientAppointmentViewModel> getPastAppointmentsForPatient(String userEmail) {
        List<PatientAppointmentsServiceModel> patientAppointmentsServiceModels = this.appointmentRepository.getPastAppointmentsForPatient(userEmail,
                LocalDate.now(), StatusEnum.CLOSED).stream().map(appointment -> {
            PatientAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, PatientAppointmentsServiceModel.class);
            serviceModel.setDoctor(String.format("%s %s", appointment.getMd().getFirstName(), appointment.getMd().getLastName()));
            serviceModel.setJob(appointment.getMd().getJob());
            serviceModel.setHospitalId(appointment.getMd().getHospitalId());
            return serviceModel;
        }).collect(Collectors.toList());

        return patientAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, PatientAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientAppointmentViewModel> getFutureAppointmentsForPatient(String userEmail) {
        List<PatientAppointmentsServiceModel> patientAppointmentsServiceModels = this.appointmentRepository.getFutureAppointmentsForPatient(userEmail,
                LocalDate.now(), StatusEnum.CONFIRMED).stream().map(appointment -> {
            PatientAppointmentsServiceModel serviceModel = this.modelMapper.map(appointment, PatientAppointmentsServiceModel.class);
            serviceModel.setDoctor(String.format("%s %s", appointment.getMd().getFirstName(), appointment.getMd().getLastName()));
            serviceModel.setJob(appointment.getMd().getJob());
            serviceModel.setHospitalId(appointment.getMd().getHospitalId());
            return serviceModel;
        }).collect(Collectors.toList());

        return patientAppointmentsServiceModels.stream().map(model -> this.modelMapper.map(model, PatientAppointmentViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public PatientAmbulatoryListDetails getPatientViewModelByAppointmentId(String id) {
        return this.modelMapper.map(this.appointmentRepository.getPatientByPatientId(id).get(), PatientAmbulatoryListDetails.class);
    }

    @Override
    public MdDocumentDetails getMdDetailsByAppointmentId(String id) {
        return this.modelMapper.map(this.appointmentRepository.getMdByMdId(id).get(), MdDocumentDetails.class);
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
        return this.modelMapper.map(this.appointmentRepository.
                getPatientByPatientId(appointmentId).get(),
                PatientPrescriptionDetails.class);
    }

    @Override
    public PatientSickLeaveDetails getSickPatientViewModelByAppointmentId(String appointmentId) {
        return this.modelMapper.map(this.appointmentRepository.
                        getPatientByPatientId(appointmentId).get(),
                PatientSickLeaveDetails.class);
    }

    @Override
    public void deleteUnconfirmedAndUnattendedAppointmentsFromDatabase() {
        this.appointmentRepository.
                deleteUnconfirmedAndUnattendedAppointmentsFromDataBase(StatusEnum.UNCONFIRMED, StatusEnum.NO_SHOW);
    }

    @Override
    public void changeStatusOfCompletedAppointmentsForTheDay() {
        this.appointmentRepository.updateTodaysConfirmedAppointmentStatus(
                StatusEnum.CONFIRMED, StatusEnum.CLOSED, LocalDate.now()
        );
    }

    @Override
    public void updateStatusofNoShowPatientsForAppointments() {
        this.appointmentRepository.updateStatusOfNoShows(StatusEnum.CONFIRMED, StatusEnum.NO_SHOW, LocalDate.now());
    }

}
