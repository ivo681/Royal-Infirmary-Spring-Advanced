package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.AppointmentRepository;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.UserDetailsService;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AppointmentControllerTest {
    //private User user1;
//    private User md;
    private String mdId;

    @Autowired
    private MockMvc mockMvc;

    private UserDetailsService userService;


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;


    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testInitialBookAppointmentPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/appointments/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookappointment"))
                .andExpect(model().attributeExists("gpId"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testChooseSpecialistPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(get("/appointments/book/choose-specialist"))
                .andExpect(status().isOk())
                .andExpect(view().name("choose-specialist"))
                .andExpect(model().attributeExists("mds"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testBookAppointmentWithMockSpecialist() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        mockMvc.perform(MockMvcRequestBuilders.get(
                "/appointments/book/{id}", mdId
        )).andExpect(status().isOk())
                .andExpect(view().name("bookappointmenttwo"))
                .andExpect(model().attributeExists("mdFullName"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testBookAppointmentWithInvalidDatePost() throws Exception {
        String userEmail = "firstmail@abv.bg";
        userService.loadUserByUsername(userEmail);
        mockMvc.perform(MockMvcRequestBuilders.post(
                "/appointments/book/{id}", mdId)
                .param("userEmail", userEmail)
                .param("mdId", mdId)
                .param("date", String.valueOf(LocalDate.now()))
                .param("reason", "")
                .param("timeSpan", "")
                .param("md", "Misho Shisho")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/appointments/book/" + mdId));
        List<Appointment> unconfirmedAppointmentsForUser = appointmentRepository.findUnconfirmedAppointmentForUser(
                LocalDate.now(), userEmail, mdId, StatusEnum.UNCONFIRMED);
        Assertions.assertTrue(unconfirmedAppointmentsForUser.isEmpty());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testBookAppointmentWithValidDataPost() throws Exception {
        String userEmail = "firstmail@abv.bg";
        userService.loadUserByUsername(userEmail);
        mockMvc.perform(MockMvcRequestBuilders.post(
                "/appointments/book/{id}", mdId)
                .param("userEmail", userEmail)
                .param("mdId", mdId)
                .param("date", String.valueOf(LocalDate.now().plusDays(2)))
                .param("reason", "Check")
                .param("timeSpan", "9:00 to 10:00")
                .param("md", "Misho Shisho")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        List<Appointment> unconfirmedAppointmentsForUser = appointmentRepository.findUnconfirmedAppointmentForUser(
                LocalDate.now().plusDays(2), userEmail, mdId, StatusEnum.UNCONFIRMED);
        Assertions.assertTrue(!unconfirmedAppointmentsForUser.isEmpty());
    }

    @BeforeAll
    public void setUp() {
        userService = new UserDetailsService(userRepository);
//        appointmentRepository.deleteAll();
//        appointmentRepository.flush();
//        userRepository.deleteAll();
//        userRepository.flush();
//        userRoleRepository.deleteAll();
//        userRoleRepository.flush();

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
        mdId = md.getId();

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
    }
}
