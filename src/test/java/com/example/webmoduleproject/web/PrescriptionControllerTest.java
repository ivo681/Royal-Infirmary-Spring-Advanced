package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.*;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.*;
import com.example.webmoduleproject.service.UserDetailsService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class PrescriptionControllerTest {
    private String appointmentId;
    @Autowired
    private MockMvc mockMvc;
    private UserDetailsService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AmbulatoryListRepository ambulatoryListRepository;

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAllPatientPrescriptionsPage() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/prescriptions/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", false));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllPrescriptionsPageForbiddenAccess() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/prescriptions/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testOwnPrescriptionsPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/prescriptions/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", true));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testDetailsPrescriptionsPage() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/prescriptions/details/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("prescription"))
                .andExpect(model().attributeExists("prescription"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testDetailsPrescriptionsPageInvalidId() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/prescriptions/details/{id}", "Invalid id"))
                .andExpect(status().is4xxClientError());
    }


    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
        UserRole mockPatient = new UserRole();
        mockPatient.setRole(RoleEnum.PATIENT);
        mockPatient = userRoleRepository.save(mockPatient);
        UserRole mockGp = new UserRole();
        mockGp.setRole(RoleEnum.GP);
        mockGp = userRoleRepository.save(mockGp);
        UserRole mockMd = new UserRole();
        mockMd.setRole(RoleEnum.MD);
        mockMd = userRoleRepository.save(mockMd);

        User md = new User();
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

        User user1 = new User();
        user1.setFirstName("Shisho");
        user1.setLastName("Bakshisho");
        user1.setEmail("firstmail@abv.bg");
        user1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        user1.setId("1");
        user1.setPassword("TopSecret123!");
        user1.setTelephone("0888888888");
        user1.setAddress("Sample address");
        user1.setIdNumber("9010100000");
        user1.setGp(md);
        user1.setRoles(List.of(mockPatient));
        user1 = userRepository.save(user1);

        Appointment appointmentActual = new Appointment();
        appointmentActual.setMd(md);
        appointmentActual.setPatient(user1);
        appointmentActual.setDate(LocalDate.now());
        appointmentActual.setReason("Test check");
        appointmentActual.setTimeSpan("9:00 to 10:00");
        appointmentActual.setStatus(StatusEnum.CONFIRMED);
        appointmentActual = appointmentRepository.save(appointmentActual);
        appointmentId = appointmentActual.getId();

        AmbulatoryList ambulatoryList = new AmbulatoryList();
        ambulatoryList.setDate(appointmentActual.getDate());
        ambulatoryList.setPatient(user1);
        ambulatoryList.setMd(md);
        ambulatoryList.setAppointment(appointmentActual);
        ambulatoryList.setMedicines("Aspirin");
        ambulatoryList.setPatientHomeAddress(user1.getAddress());
        ambulatoryList.setMdTelephoneNumber(md.getTelephone());
        ambulatoryList.setPatientTelephoneNumber(user1.getTelephone());
        ambulatoryList.setDiagnosis("Test");
        ambulatoryList.setNumber(1L);
        ambulatoryList = ambulatoryListRepository.save(ambulatoryList);

        Prescription prescription = new Prescription();
        prescription.setMd(md);
        prescription.setAppointment(appointmentActual);
        prescription.setPatient(user1);
        prescription.setNumber(1L);
        prescription.setMedicines(ambulatoryList.getMedicines());
        prescription.setDate(ambulatoryList.getDate());
        prescription.setMdTelephoneNumber(ambulatoryList.getMdTelephoneNumber());
        prescription.setPatientHomeAddress(ambulatoryList.getPatientHomeAddress());
        prescriptionRepository.save(prescription);
    }
}
