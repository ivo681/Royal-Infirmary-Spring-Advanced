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
    private String mockAppointmentId;

    private DataSetup dataSetup;
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
        this.mockMvc.perform(get("/prescriptions/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", false));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllPrescriptionsPageForbiddenAccess() throws Exception {
        this.mockMvc.perform(get("/prescriptions/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testOwnPrescriptionsPage() throws Exception {
        this.mockMvc.perform(get("/prescriptions/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", true));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testDetailsPrescriptionsPage() throws Exception {
        this.mockMvc.perform(get("/prescriptions/details/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("prescription"))
                .andExpect(model().attributeExists("prescription"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testDetailsPrescriptionsPageInvalidId() throws Exception {
        this.mockMvc.perform(get("/prescriptions/details/{id}", mockAppointmentId))
                .andExpect(status().is4xxClientError());
    }


    @BeforeEach
    public void setUp() {
        dataSetup = new DataSetup(userRepository, userRoleRepository,
                appointmentRepository, ambulatoryListRepository, prescriptionRepository);
        dataSetup.BasicDataSetUp();
        dataSetup.AppointmentDataSetup();
        dataSetup.AmbulatoryListDataSetup();
        dataSetup.PrescriptionDataSetup();
        appointmentId = dataSetup.getAppointmentId();
        mockAppointmentId = dataSetup.getMockAppointmentId();
    }
}
