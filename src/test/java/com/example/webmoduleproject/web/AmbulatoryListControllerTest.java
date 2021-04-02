package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.AmbulatoryList;
import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.*;
import com.example.webmoduleproject.service.UserDetailsService;
import org.junit.jupiter.api.*;
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
public class AmbulatoryListControllerTest {
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
    public void testAllPatientAmbulatoryListsPage() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-lists"))
                .andExpect(model().attribute("own", false));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testOwnAmbulatoryListsPage() throws Exception {
        //userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-lists"))
                .andExpect(model().attribute("own", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllAmbulatoryListsAccess() throws Exception {
        //userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testBuildNewAmbulatoryListWithForbiddenAccess() throws Exception {
        //userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/new/{id}", appointmentId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewAmbulatoryListWithInvalidAppointment() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/new/{id}", "someWrongId"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewAmbulatoryListPage() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-lists/new/{id}", mockAppointmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory"))
                .andExpect(model().attributeExists("mdViewModel"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryList() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(post("/ambulatory-lists/create-{id}", mockAppointmentId)
        .param("diagnosis", "Test diagnosis")
        .param("medicines", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-lists/details/" + mockAppointmentId));

        Assertions.assertTrue(ambulatoryListRepository.findByAppointmentId(mockAppointmentId).isPresent());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListInvalidData() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(post("/ambulatory-lists/create-{id}", mockAppointmentId)
                .param("diagnosis", "Te")
                .param("medicines", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-lists/new/" + mockAppointmentId));

        Assertions.assertTrue(ambulatoryListRepository.findByAppointmentId(mockAppointmentId).isEmpty());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListAutomaticPrescription() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(mockAppointmentId).isEmpty());

        this.mockMvc.perform(post("/ambulatory-lists/create-{id}", mockAppointmentId)
                .param("diagnosis", "Test diagnosis")
                .param("medicines", "Aspirin")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-lists/details/" + mockAppointmentId));

        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(mockAppointmentId).isPresent());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListAutomaticPrescriptionValidation() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");
        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(mockAppointmentId).isEmpty());

        this.mockMvc.perform(post("/ambulatory-lists/create-{id}", mockAppointmentId)
                .param("diagnosis", "Test diagnosis")
                .param("medicines", "         ")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-lists/details/" + mockAppointmentId));

        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(mockAppointmentId).isEmpty());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testGetAmbulatoryListDetailsPageNotExisting() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-lists/details/{id}", mockAppointmentId)
        )
                .andExpect(status().is4xxClientError());
    }



    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testGetAmbulatoryListDetailsPageWithMd() throws Exception {
        //userService.loadUserByUsername("secondmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-lists/details/{id}", appointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-confirm"))
                .andExpect(model().attributeExists("ambulatoryList"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testGetAmbulatoryListDetailsPageWithPatient() throws Exception {
        //userService.loadUserByUsername("firstmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-lists/details/{id}", appointmentId)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-confirm"))
                .andExpect(model().attributeExists("ambulatoryList"));
    }

    @BeforeEach
    public void setUp() {
        //userService = new UserDetailsService(userRepository);
        dataSetup = new DataSetup(userRepository, userRoleRepository,
                appointmentRepository, ambulatoryListRepository);
        dataSetup.BasicDataSetUp();
        dataSetup.AppointmentDataSetup();
        dataSetup.AmbulatoryListDataSetup();
        appointmentId = dataSetup.getAppointmentId();
        mockAppointmentId = dataSetup.getMockAppointmentId();
    }
}
