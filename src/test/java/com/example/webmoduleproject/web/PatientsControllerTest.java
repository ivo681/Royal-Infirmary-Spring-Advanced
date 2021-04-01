package com.example.webmoduleproject.web;

import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class PatientsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private DataSetup dataSetup;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testPatientsPage() throws Exception {
        this.mockMvc.perform(get("/patients/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("patientslist"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAppointmentNavPage() throws Exception {
        this.mockMvc.perform(get("/patients/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-nav"))
                .andExpect(model().attribute("enableMdSection", true));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAppointmentsPastPage() throws Exception {
        this.mockMvc.perform(get("/patients/appointments/past"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list"))
                .andExpect(model().attribute("past", true));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAppointmentsTodayPage() throws Exception {
        this.mockMvc.perform(get("/patients/appointments/today"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list"))
                .andExpect(model().attribute("today", true));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAppointmentsFutureNavPage() throws Exception {
        this.mockMvc.perform(get("/patients/appointments/future"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list"))
                .andExpect(model().attribute("future", true));
    }

    @BeforeEach
    public void setUp() {
        dataSetup = new DataSetup(userRepository, userRoleRepository);
        dataSetup.BasicDataSetUp();
    }
}
