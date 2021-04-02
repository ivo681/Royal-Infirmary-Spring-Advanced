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
    private SickLeaveRepository sickLeaveRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AmbulatoryListRepository ambulatoryListRepository;

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAllPatientSickLeavesPage() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", false))
                .andExpect(model().attributeExists("sickLeaves"));

    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllPatientSickLeavesForbiddenAccess() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testOwnSickLeavesPage() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", true))
                .andExpect(model().attributeExists("sickLeaves"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewSickLeave() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/new/{id}", mockAppointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leave"))
                .andExpect(model().attributeExists("sickLeaveBindingModel"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testBuildNewSickLeaveForbiddenAccess() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/new/{id}", mockAppointmentId)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testExistingSickLeavePreventBuild() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/new/{id}", appointmentId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/details/" + appointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateSickLeavePost() throws Exception {
        this.mockMvc.perform(post("/sick-leaves/create-{id}", mockAppointmentId)
                .param("reason", "Test reason")
                .param("fromDate", String.valueOf(LocalDate.now()))
                .param("toDate", String.valueOf(LocalDate.now().plusDays(2)))
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/details/" + mockAppointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateSickLeaveWithInvalidData() throws Exception {
        this.mockMvc.perform(post("/sick-leaves/create-{id}", mockAppointmentId)
                .param("reason", "Test reason")
                .param("fromDate", String.valueOf(LocalDate.now().plusDays(2)))
                .param("toDate", String.valueOf(LocalDate.now()))
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sick-leaves/new/" + mockAppointmentId));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testShowSickLeaveDetails() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/details/{id}", appointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name( "sick-leave-confirm"))
                .andExpect(model().attributeExists("sickLeaveView"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testShowSickLeaveDetailsWithInvalidId() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/details/{id}", mockAppointmentId)
        )
                .andExpect(status().is4xxClientError());
    }

    @BeforeEach
    public void setUp() {
        dataSetup = new DataSetup(userRepository, userRoleRepository,
                appointmentRepository, ambulatoryListRepository, sickLeaveRepository);
        dataSetup.BasicDataSetUp();
        dataSetup.AppointmentDataSetup();
        dataSetup.AmbulatoryListDataSetup();
        dataSetup.SickLeaveDataSetup();
        appointmentId = dataSetup.getAppointmentId();
        mockAppointmentId = dataSetup.getMockAppointmentId();
    }

}
