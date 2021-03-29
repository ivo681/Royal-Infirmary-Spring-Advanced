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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:hsqldb:mem:${random.uuid}"
//})
//@SpringBootTest
//@AutoConfigureMockMvc
public class AmbulatoryListControllerTest {
    private String confirmedAppointmentId;
    private String idForAmbulatoryList;
    private String ambulatoryListId;
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
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-lists"))
                .andExpect(model().attribute("own", false));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testOwnAmbulatoryListsPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-lists"))
                .andExpect(model().attribute("own", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testAllAmbulatoryListsAccess() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testBuildNewAmbulatoryListWithForbiddenAccess() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/new/{id}", confirmedAppointmentId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewAmbulatoryListWithInvalidAppointment() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/new/{id}", "someWrongId"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testBuildNewAmbulatoryListPage() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/new/{id}", confirmedAppointmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory"))
                .andExpect(model().attributeExists("mdViewModel"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryList() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/create-{id}", confirmedAppointmentId)
        .param("diagnosis", "Test diagnosis")
        .param("medicines", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-list/details/" + confirmedAppointmentId));

        Assertions.assertTrue(ambulatoryListRepository.findByAppointmentId(confirmedAppointmentId).isPresent());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListInvalidData() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/ambulatory-list/create-{id}", confirmedAppointmentId)
                .param("diagnosis", "Te")
                .param("medicines", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-list/new/" + confirmedAppointmentId));

        Assertions.assertTrue(ambulatoryListRepository.findByAppointmentId(confirmedAppointmentId).isEmpty());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListAutomaticPrescription() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(confirmedAppointmentId).isEmpty());

        this.mockMvc.perform(get("/ambulatory-list/create-{id}", confirmedAppointmentId)
                .param("diagnosis", "Test diagnosis")
                .param("medicines", "Aspirin")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-list/details/" + confirmedAppointmentId));

        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(confirmedAppointmentId).isPresent());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testCreateNewAmbulatoryListAutomaticPrescriptionValidation() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(confirmedAppointmentId).isEmpty());

        this.mockMvc.perform(get("/ambulatory-list/create-{id}", confirmedAppointmentId)
                .param("diagnosis", "Test diagnosis")
                .param("medicines", "         ")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/ambulatory-list/details/" + confirmedAppointmentId));

        Assertions.assertTrue(prescriptionRepository.findByAppointmentId(confirmedAppointmentId).isEmpty());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testGetAmbulatoryListDetailsPageNotExisting() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-list/details/{id}", confirmedAppointmentId)
        )
                .andExpect(status().is4xxClientError());
    }



    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testGetAmbulatoryListDetailsPageWithMd() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-list/details/{id}", idForAmbulatoryList)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-confirm"))
                .andExpect(model().attributeExists("ambulatoryList"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testGetAmbulatoryListDetailsPageWithPatient() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");

        this.mockMvc.perform(get("/ambulatory-list/details/{id}", idForAmbulatoryList)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ambulatory-confirm"))
                .andExpect(model().attributeExists("ambulatoryList"));
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

        Appointment appointmentMock = new Appointment();
        appointmentMock.setMd(md);
        appointmentMock.setPatient(user1);
        appointmentMock.setDate(LocalDate.now());
        appointmentMock.setReason("Test check");
        appointmentMock.setTimeSpan("9:00 to 10:00");
        appointmentMock.setStatus(StatusEnum.CONFIRMED);
        appointmentMock = appointmentRepository.save(appointmentMock);
        confirmedAppointmentId = appointmentMock.getId();

        Appointment appointmentActual = new Appointment();
        appointmentActual.setMd(md);
        appointmentActual.setPatient(user1);
        appointmentActual.setDate(LocalDate.now());
        appointmentActual.setReason("Test check");
        appointmentActual.setTimeSpan("9:00 to 10:00");
        appointmentActual.setStatus(StatusEnum.CONFIRMED);
        appointmentActual = appointmentRepository.save(appointmentActual);
        idForAmbulatoryList = appointmentActual.getId();

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
        ambulatoryListId = ambulatoryList.getId();
    }
}
