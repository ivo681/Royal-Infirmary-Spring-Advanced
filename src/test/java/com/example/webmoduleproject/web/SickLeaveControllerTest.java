package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.*;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.*;
import com.example.webmoduleproject.service.UserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class SickLeaveControllerTest {
    private String appointmentId;
    private String ambulatoryListId;
    private User user1;
    private User md;
    private AmbulatoryList ambulatoryList;
    private Appointment appointment;
    @Autowired
    private MockMvc mockMvc;
    private UserDetailsService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private SickLeaveRepository sickLeaveRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AmbulatoryListRepository ambulatoryListRepository;

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAllPatientSickLeavesPage() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", false))
                .andExpect(model().attributeExists("sickLeaves"));

    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllPatientSickLeavesForbiddenAccess() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testOwnSickLeavesPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", true))
                .andExpect(model().attributeExists("sickLeaves"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewSickLeave() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/new/{id}", appointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leave"))
                .andExpect(model().attributeExists("sickLeaveBindingModel"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testBuildNewSickLeaveForbiddenAccess() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/new/{id}", appointmentId)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testExistingSickLeavePreventBuild() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        createSickLeaveForAppointment();
        this.mockMvc.perform(get("/sick-leaves/new/{id}", appointmentId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/details/" + appointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateSickLeavePost() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/create-{id}", appointmentId)
                .param("reason", "Test reason")
                .param("fromDate", String.valueOf(LocalDate.now()))
                .param("toDate", String.valueOf(LocalDate.now().plusDays(2)))
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/details/" + appointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateSickLeaveWithInvalidData() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/create-{id}", appointmentId)
                .param("reason", "Test reason")
                .param("fromDate", String.valueOf(LocalDate.now().plusDays(2)))
                .param("toDate", String.valueOf(LocalDate.now()))
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/new/" + appointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testShowSickLeaveDetails() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        createSickLeaveForAppointment();
        this.mockMvc.perform(get("/sick-leaves/details/{id}", appointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name( "sick-leave-confirm"))
                .andExpect(model().attributeExists("sickLeaveView"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testShowSickLeaveDetailsWithInvalidId() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/sick-leaves/details/{id}", "Invalid")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/error"));
    }

    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);

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
        md.setFirstName("Misho");
        md.setLastName("Shisho");
        md.setEmail("secondmail@abv.bg");
        md.setDateOfBirth(LocalDate.of(1990, 10, 10));
        md.setId("2");
        md.setHospitalId(1L);
        md.setPassword("TopSecret123!");
        md.setJob("General Practitioner");
        md.setTelephone("0888888888");
        md.setAddress("Sample address");
        md.setIdNumber("9010100000");
        md.setRoles(List.of(mockPatient, mockGp, mockMd));
        md = userRepository.save(md);

        user1 = new User();
        user1.setFirstName("Shisho");
        user1.setLastName("Bakshisho");
        user1.setEmail("firstmail@abv.bg");
        user1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        user1.setId("1");
        user1.setPassword("TopSecret123!");
        user1.setTelephone("0888888888");
        user1.setAddress("Sample address");
        user1.setIdNumber("9010100000");
        user1.setJob("Test job");
        user1.setEmployer("Test employer");
        user1.setGp(md);
        user1.setRoles(List.of(mockPatient));
        user1 = userRepository.save(user1);

        appointment = new Appointment();
        appointment.setMd(md);
        appointment.setPatient(user1);
        appointment.setDate(LocalDate.now());
        appointment.setReason("Test check");
        appointment.setTimeSpan("9:00 to 10:00");
        appointment.setStatus(StatusEnum.CONFIRMED);
        appointment = appointmentRepository.save(appointment);
        appointmentId = appointment.getId();

        ambulatoryList = new AmbulatoryList();
        ambulatoryList.setDate(appointment.getDate());
        ambulatoryList.setPatient(user1);
        ambulatoryList.setMd(md);
        ambulatoryList.setAppointment(appointment);
        ambulatoryList.setMedicines("Aspirin");
        ambulatoryList.setPatientHomeAddress(user1.getAddress());
        ambulatoryList.setMdTelephoneNumber(md.getTelephone());
        ambulatoryList.setPatientTelephoneNumber(user1.getTelephone());
        ambulatoryList.setDiagnosis("Test");
        ambulatoryList.setNumber(1L);
        ambulatoryList = ambulatoryListRepository.save(ambulatoryList);
        ambulatoryListId = ambulatoryList.getId();
    }

    void createSickLeaveForAppointment(){
        SickLeave sickLeave = new SickLeave();
        sickLeave.setPatient(user1);
        sickLeave.setAppointment(appointment);
        sickLeave.setMd(md);
        sickLeave.setReason("Test reason");
        sickLeave.setNumber(1L);
        sickLeave.setPatientHomeAddress(user1.getAddress());
        sickLeave.setMdTelephoneNumber(md.getTelephone());
        sickLeave.setPatientTelephoneNumber(user1.getTelephone());
        sickLeave.setPatientJob(user1.getJob());
        sickLeave.setPatientEmployer(user1.getEmployer());
        sickLeave.setDiagnosis(ambulatoryList.getDiagnosis());
        sickLeave.setFromDate(LocalDate.now());
        sickLeave.setToDate(LocalDate.now().plusDays(2));
        sickLeave = sickLeaveRepository.save(sickLeave);
        String sickLeaveId = sickLeave.getId();
    }


}
