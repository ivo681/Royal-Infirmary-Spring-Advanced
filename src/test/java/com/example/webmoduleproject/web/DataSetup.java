package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.*;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.*;

import java.time.LocalDate;
import java.util.List;

class DataSetup {
    private String appointmentId;
    private String md1Id;
    private String md2Id;
    private String userId;
    private String ambulatoryListId;
    private String mockAppointmentId;

    private User md;
    private User md2;
    private User user1;
    private Appointment appointment;
    private Appointment mockAppointment;
    private AmbulatoryList ambulatoryList;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private AppointmentRepository appointmentRepository;
    private AmbulatoryListRepository ambulatoryListRepository;
    private PrescriptionRepository prescriptionRepository;
    private SickLeaveRepository sickLeaveRepository;

    public DataSetup(UserRepository userRepository,
                     UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public DataSetup(UserRepository userRepository,
                     UserRoleRepository userRoleRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public DataSetup(UserRepository userRepository,
                     UserRoleRepository userRoleRepository, AppointmentRepository appointmentRepository,
                     AmbulatoryListRepository ambulatoryListRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.appointmentRepository = appointmentRepository;
        this.ambulatoryListRepository = ambulatoryListRepository;
    }

    public DataSetup(UserRepository userRepository,
                     UserRoleRepository userRoleRepository, AppointmentRepository appointmentRepository,
                     AmbulatoryListRepository ambulatoryListRepository, PrescriptionRepository prescriptionRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.appointmentRepository = appointmentRepository;
        this.ambulatoryListRepository = ambulatoryListRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public DataSetup(UserRepository userRepository,
                     UserRoleRepository userRoleRepository, AppointmentRepository appointmentRepository,
                     AmbulatoryListRepository ambulatoryListRepository, SickLeaveRepository sickLeaveRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.appointmentRepository = appointmentRepository;
        this.ambulatoryListRepository = ambulatoryListRepository;
        this.sickLeaveRepository = sickLeaveRepository;
    }

    public void BasicDataSetUp() {
        UserRole mockAdmin = new UserRole();
        mockAdmin.setRole(RoleEnum.ADMIN);
        mockAdmin = userRoleRepository.save(mockAdmin);
        UserRole mockPatient = new UserRole();
        mockPatient.setRole(RoleEnum.PATIENT);
        mockPatient = userRoleRepository.save(mockPatient);
        UserRole mockGp = new UserRole();
        mockGp.setRole(RoleEnum.GP);
        mockGp = userRoleRepository.save(mockGp);
        UserRole mockMd = new UserRole();
        mockMd.setRole(RoleEnum.MD);
        mockMd = userRoleRepository.save(mockMd);

        md = new User();
        md.setFirstName("Tisho");
        md.setLastName("Shishov");
        md.setEmail("thirdmail@abv.bg");
        md.setDateOfBirth(LocalDate.of(1990, 10, 10));
        md.setId("3");
        md.setHospitalId(3L);
        md.setPassword("TopSecret123!");
        md.setJob("General Practitioner");
        md.setTelephone("0888888878");
        md.setAddress("Sample address3");
        md.setIdNumber("9010100004");
        md.setRoles(List.of(mockPatient, mockGp, mockMd));
        md = userRepository.save(md);
        md1Id = md.getId();

        md2 = new User();
        md2.setFirstName("Misho");
        md2.setLastName("Shisho");
        md2.setEmail("secondmail@abv.bg");
        md2.setDateOfBirth(LocalDate.of(1990, 10, 10));
        md2.setId("2");
        md2.setHospitalId(1L);
        md2.setGp(md);
        md2.setPassword("TopSecret123!");
        md2.setJob("General Practitioner");
        md2.setTelephone("0888888868");
        md2.setAddress("Sample address");
        md2.setIdNumber("9010100000");
        md2.setRoles(List.of(mockPatient, mockGp, mockMd));
        md2 = userRepository.save(md2);
        md2Id = md2.getId();

        user1 = new User();
        user1.setFirstName("Shisho");
        user1.setLastName("Bakshisho");
        user1.setEmail("firstmail@abv.bg");
        user1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        user1.setId("1");
        user1.setGp(md);
        user1.setPassword("TopSecret123!");
        user1.setTelephone("0888888888");
        user1.setAddress("Sample address");
        user1.setIdNumber("9010100000");
        user1.setEmployer("Test employer");
        user1.setJob("Test job");
        user1.setRoles(List.of(mockPatient));
        user1 = userRepository.save(user1);
        userId = user1.getId();
    }

    public void AppointmentDataSetup() {
        mockAppointment = new Appointment();
        mockAppointment.setMd(md2);
        mockAppointment.setPatient(user1);
        mockAppointment.setDate(LocalDate.now().plusDays(1));
        mockAppointment.setReason("Test check");
        mockAppointment.setTimeSpan("9:00 to 10:00");
        mockAppointment.setStatus(StatusEnum.UNCONFIRMED);
        mockAppointment = appointmentRepository.save(mockAppointment);
        mockAppointmentId = mockAppointment.getId();
    }

    public void AmbulatoryListDataSetup() {
        mockAppointment.setStatus(StatusEnum.CONFIRMED);
        mockAppointment = appointmentRepository.save(mockAppointment);
        mockAppointmentId = mockAppointment.getId();

        appointment = new Appointment();
        appointment.setMd(md2);
        appointment.setPatient(user1);
        appointment.setDate(LocalDate.now());
        appointment.setReason("Test check");
        appointment.setTimeSpan("9:00 to 10:00");
        appointment.setStatus(StatusEnum.CONFIRMED);
        appointment = appointmentRepository.save(appointment);
        appointmentId = appointment.getId();

        ambulatoryList = new AmbulatoryList();
        ambulatoryList.setDate(appointment.getDate());
        ambulatoryList.setPatient(appointment.getPatient());
        ambulatoryList.setMd(appointment.getMd());
        ambulatoryList.setAppointment(appointment);
        ambulatoryList.setMedicines("Aspirin");
        ambulatoryList.setPatientHomeAddress(appointment.getPatient().getAddress());
        ambulatoryList.setMdTelephoneNumber(appointment.getMd().getTelephone());
        ambulatoryList.setPatientTelephoneNumber(appointment.getPatient().getTelephone());
        ambulatoryList.setDiagnosis("Test");
        ambulatoryList.setNumber(1L);
        ambulatoryList = ambulatoryListRepository.save(ambulatoryList);
        ambulatoryListId = ambulatoryList.getId();
    }

    public void PrescriptionDataSetup() {
        Prescription prescription = new Prescription();
        prescription.setMd(appointment.getMd());
        prescription.setAppointment(appointment);
        prescription.setPatient(appointment.getPatient());
        prescription.setNumber(1L);
        prescription.setMedicines(ambulatoryList.getMedicines());
        prescription.setDate(ambulatoryList.getDate());
        prescription.setMdTelephoneNumber(ambulatoryList.getMdTelephoneNumber());
        prescription.setPatientHomeAddress(ambulatoryList.getPatientHomeAddress());
        prescriptionRepository.save(prescription);
    }

    public void SickLeaveDataSetup() {
        AmbulatoryList mockAppointmentList = new AmbulatoryList();
        mockAppointmentList.setDate(mockAppointment.getDate());
        mockAppointmentList.setPatient(mockAppointment.getPatient());
        mockAppointmentList.setMd(mockAppointment.getMd());
        mockAppointmentList.setAppointment(mockAppointment);
        mockAppointmentList.setMedicines("Aspirin");
        mockAppointmentList.setPatientHomeAddress(mockAppointment.getPatient().getAddress());
        mockAppointmentList.setMdTelephoneNumber(mockAppointment.getMd().getTelephone());
        mockAppointmentList.setPatientTelephoneNumber(mockAppointment.getPatient().getTelephone());
        mockAppointmentList.setDiagnosis("Test");
        mockAppointmentList.setNumber(5L);
        mockAppointmentList = ambulatoryListRepository.save(mockAppointmentList);

        SickLeave sickLeave = new SickLeave();
        sickLeave.setAppointment(appointment);
        sickLeave.setPatient(appointment.getPatient());
        sickLeave.setMd(appointment.getMd());
        sickLeave.setReason("Test reason");
        sickLeave.setNumber(1L);
        sickLeave.setPatientHomeAddress(appointment.getPatient().getAddress());
        sickLeave.setMdTelephoneNumber(appointment.getMd().getTelephone());
        sickLeave.setPatientTelephoneNumber(appointment.getPatient().getTelephone());
        sickLeave.setPatientJob(appointment.getPatient().getJob());
        sickLeave.setPatientEmployer(appointment.getPatient().getEmployer());
        sickLeave.setDiagnosis(ambulatoryList.getDiagnosis());
        sickLeave.setFromDate(LocalDate.now());
        sickLeave.setToDate(LocalDate.now().plusDays(2));
        sickLeaveRepository.save(sickLeave);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getMd2Id() {
        return md2Id;
    }

    public String getMd1Id() {
        return md1Id;
    }

    public String getUserId() {
        return userId;
    }

    public String getAmbulatoryListId() {
        return ambulatoryListId;
    }

    public String getMockAppointmentId() {
        return mockAppointmentId;
    }
}
