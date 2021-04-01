package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import com.example.webmoduleproject.repository.AppointmentRepository;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.UserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class AppointmentControllerTest {
    private String mockAppointmentId;
    private String mdId;

    @Autowired
    private MockMvc mockMvc;

    private UserDetailsService userService;

    private DataSetup dataSetup;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;


    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testInitialBookAppointmentPage() throws Exception {
        this.mockMvc.perform(get("/appointments/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookappointment"))
                .andExpect(model().attributeExists("gpId"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testChooseSpecialistPage() throws Exception {
        this.mockMvc.perform(get("/appointments/book/choose-specialist"))
                .andExpect(status().isOk())
                .andExpect(view().name("choose-specialist"))
                .andExpect(model().attributeExists("mds"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testBookAppointmentWithMockSpecialist() throws Exception {
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
        Assertions.assertFalse(unconfirmedAppointmentsForUser.isEmpty());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testConfirmAppointment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(
                "/appointments/confirm/{id}", mockAppointmentId)
                .param("action", "confirm")
                .param("date", String.valueOf(LocalDate.now().plusDays(1)))
                .param("timeSpan", "9:00 to 10:00")
                .param("md", "Misho Shisho")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"));

        Appointment confirmedAppointment = appointmentRepository.findById(mockAppointmentId).get();
        Assertions.assertEquals(StatusEnum.CONFIRMED, confirmedAppointment.getStatus());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testCancelAppointment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(
                "/appointments/confirm/{id}", mockAppointmentId)
                .param("action", "cancel")
                .param("date", String.valueOf(LocalDate.now().plusDays(1)))
                .param("timeSpan", "9:00 to 10:00")
                .param("md", "Misho Shisho")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/appointments/book"));

        Optional<Appointment> confirmedAppointment = appointmentRepository.findById(mockAppointmentId);
        Assertions.assertTrue(confirmedAppointment.isEmpty());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    void testUnconfirmedAppointmentShow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                "/appointments/confirm/{id}", mockAppointmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("appointmentconfirm"))
                .andExpect(model().attributeExists("unconfirmedAppointmentById"));

        Appointment confirmedAppointment = appointmentRepository.findById(mockAppointmentId).get();
        Assertions.assertEquals(StatusEnum.UNCONFIRMED, confirmedAppointment.getStatus());
    }

    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
        dataSetup = new DataSetup(userRepository, userRoleRepository, appointmentRepository);
        dataSetup.BasicDataSetUp();
        dataSetup.AppointmentDataSetup();
        mockAppointmentId = dataSetup.getMockAppointmentId();
        mdId = dataSetup.getMd2Id();
    }
}
