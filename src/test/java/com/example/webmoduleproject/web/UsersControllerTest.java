package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.AmbulatoryList;
import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class UsersControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private String gp1Id;
    private String gp2Id;
    private String confirmedAppointmentId;
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
    public void testRegisterPage() throws Exception {
        this.mockMvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterBindingModel"));
    }

    @Test
    public void testLoginPage() throws Exception {
        this.mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }


    @Test
    public void testLoginErrorPage() throws Exception {
        RequestBuilder requestBuilder = post("/users/login")
                .param("username", "abcd@abv.bg")
                .param("password", "1234");

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testChangeGpPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/change-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("choosegp"))
                .andExpect(model().attributeExists("allGps"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testChangeGpWithNew() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");

        Assertions.assertEquals(userRepository.
                findByEmail("firstmail@abv.bg").
                get().getGp().getId(), gp1Id
        );

        this.mockMvc.perform(get("/users/change-gp/{id}", gp2Id))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name( "redirect:/home"));

        Assertions.assertEquals(userRepository.
                findByEmail("firstmail@abv.bg").
                get().getGp().getId(), gp2Id
        );
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testPatientAppointmentNavPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/users/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-nav"))
                .andExpect(model().attribute("enablePatientSection", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testPastPatientPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/users/appointments/past"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list-patients"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attribute("past", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testFuturePatientAppointmentsPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/users/appointments/future"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list-patients"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attribute("future", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testCancelExistingAppointment() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/users/appointments/cancel-{id}", confirmedAppointmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/appointments/future"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testCancelAppointmentInvalidId() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/users/appointments/cancel-{id}", "Invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserDetailsPage() throws Exception {
        userService.loadUserByUsername("secondmail@abv.bg");
        this.mockMvc.perform(get("/users/details/{id}",gp2Id ))
                .andExpect(status().isOk())
                .andExpect(view().name("person-details"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    public void testCreateNewUserWithInvalidData() throws Exception {
        String newUserEmail = "fourthmail@abv.bg";
        Assertions.assertTrue(userRepository.findByEmail(newUserEmail).isEmpty());

        this.mockMvc.perform(post("/users/register")
                .param("firstName", "Te")
                .param("lastName", "         ")
                .param("dateOfBirth", String.valueOf(LocalDate.now()))
                .param("email", newUserEmail)
                .param("password", "123456789")
                .param("confirmPassword", "987654321")
                .param("position", "doctor")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        Assertions.assertTrue(userRepository.findByEmail(newUserEmail).isEmpty());
    }

    @Test
    public void testCreateNewUserWithExistingEmail() throws Exception {
        String newUserEmail = "secondmail@abv.bg";
        Assertions.assertTrue(userRepository.findByEmail(newUserEmail).isPresent());

        this.mockMvc.perform(post("/users/register")
                .param("firstName", "Shishko")
                .param("lastName", "Shishkov")
                .param("dateOfBirth", String.valueOf(LocalDate.of(1990, 10, 10)))
                .param("email", newUserEmail)
                .param("password", "TopSecret12!")
                .param("confirmPassword", "TopSecret12!")
                .param("position", "doctor")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        Assertions.assertNotEquals(userRepository.
                findByEmail(newUserEmail).get().getFirstName(), "Shishko");
    }

//    @Test
//    public void testCreateNewUserWithValidData() throws Exception {
//        String newUserEmail = "fifthmail@abv.bg";
////        Assertions.assertTrue(userRepository.findByEmail(newUserEmail).isEmpty());
//
//        this.mockMvc.perform(post("/users/register")
//                .param("firstName", "Shishko")
//                .param("lastName", "Shishkov")
//                .param("dateOfBirth", String.valueOf(LocalDate.of(1990, 10, 10)))
//                .param("email", newUserEmail)
//                .param("password", "TopSecret12!")
//                .param("confirmPassword", "TopSecret12!")
//                .param("position", "patient")
//                .with(csrf())
//        )
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/complete-profile"));
//
////        Assertions.assertTrue(userRepository.findByEmail(newUserEmail).isPresent());
//    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()).build();
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

        User gp1 = new User();
        gp1.setFirstName("Misho");
        gp1.setLastName("Shisho");
        gp1.setEmail("secondmail@abv.bg");
        gp1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        gp1.setId("2");
        gp1.setHospitalId(1L);
        gp1.setPassword("TopSecret123!");
        gp1.setEmployer("Test Employer");
        gp1.setJob("General Practitioner");
        gp1.setTelephone("0888888888");
        gp1.setAddress("Sample address");
        gp1.setIdNumber("9010100000");
        gp1.setRoles(List.of(mockPatient, mockGp, mockMd));
        gp1 = userRepository.save(gp1);
        gp1Id = gp1.getId();

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
        user1.setGp(gp1);
        user1.setRoles(List.of(mockPatient));
        user1 = userRepository.save(user1);


        User gp2 = new User();
        gp2.setFirstName("Tisho");
        gp2.setLastName("Shisho");
        gp2.setEmail("thirdmail@abv.bg");
        gp2.setDateOfBirth(LocalDate.of(1990, 10, 10));
        gp2.setId("2");
        gp2.setGp(gp1);
        gp2.setHospitalId(1L);
        gp2.setPassword("TopSecret123!");
        gp2.setEmployer("Test Employer");
        gp2.setJob("General Practitioner");
        gp2.setTelephone("0888888888");
        gp2.setAddress("Sample address");
        gp2.setIdNumber("9010100000");
        gp2.setRoles(List.of(mockPatient, mockGp, mockMd));
        gp2 = userRepository.save(gp2);
        gp2Id = gp2.getId();

        Appointment appointmentMock = new Appointment();
        appointmentMock.setMd(gp1);
        appointmentMock.setPatient(user1);
        appointmentMock.setDate(LocalDate.now().plusDays(2));
        appointmentMock.setReason("Test check");
        appointmentMock.setTimeSpan("9:00 to 10:00");
        appointmentMock.setStatus(StatusEnum.CONFIRMED);
        appointmentMock = appointmentRepository.save(appointmentMock);
        confirmedAppointmentId = appointmentMock.getId();
    }

}
