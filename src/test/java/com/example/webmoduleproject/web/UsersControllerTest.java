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
    private String userId;
    private String confirmedAppointmentId;
    private DataSetup dataSetup;
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
        this.mockMvc.perform(get("/change-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("choosegp"))
                .andExpect(model().attributeExists("allGps"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testChangeGpWithNew() throws Exception {
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
        this.mockMvc.perform(get("/users/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-nav"))
                .andExpect(model().attribute("enablePatientSection", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testPastPatientPage() throws Exception {
        this.mockMvc.perform(get("/users/appointments/past"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list-patients"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attribute("past", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testFuturePatientAppointmentsPage() throws Exception {
        this.mockMvc.perform(get("/users/appointments/future"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointment-list-patients"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attribute("future", true));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testCancelExistingAppointment() throws Exception {
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
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserDetailsPage() throws Exception {
        this.mockMvc.perform(get("/users/details/{id}", userId ))
                .andExpect(status().isOk())
                .andExpect(view().name("person-details"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserEditProfilePage() throws Exception {
        this.mockMvc.perform(get("/users/edit-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-person-details"))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeExists("employmentDetailsBindingModel"))
                .andExpect(model().attributeExists("contactDetailsBindingModel"));
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserEditProfileContactsValidDataPost() throws Exception {
        Assertions.assertNotEquals(this.userRepository.findByEmail("thirdmail@abv.bg").get()
        .getTelephone(), "8888855555");

        this.mockMvc.perform(post("/users/edit-profile-contacts")
                .param("newTelephone" , "8888855555")
                .param("newAddress", "Test address")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        Assertions.assertEquals(this.userRepository.findByEmail("thirdmail@abv.bg").get()
                .getTelephone(), "8888855555");
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserEditProfileContactsInvalidDataPost() throws Exception {
        String currTelephone = this.userRepository.findByEmail("thirdmail@abv.bg").get()
                .getTelephone();

        this.mockMvc.perform(post("/users/edit-profile-contacts")
                .param("newTelephone" , "8888855555888888888888888")
                .param("newAddress", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        Assertions.assertEquals(this.userRepository.findByEmail("thirdmail@abv.bg").get()
                .getTelephone(), currTelephone);
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testUserEditProfileEmploymentInvalidDataPost() throws Exception {
        String job = this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob();

        this.mockMvc.perform(post("/users/edit-profile-employment")
                .param("newEmployer" , "")
                .param("newJob", "   Freelancer  ")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        Assertions.assertEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob(), job);
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testUserEditProfileEmploymentEmptyDataPost() throws Exception {
        String job = this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob();

        this.mockMvc.perform(post("/users/edit-profile-employment")
                .param("newEmployer" , "")
                .param("newJob", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        Assertions.assertNull(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testUserEditProfileEmploymentNewDataPost() throws Exception {
        String job = this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob();

        this.mockMvc.perform(post("/users/edit-profile-employment")
                .param("newEmployer" , "New employer")
                .param("newJob", "New job")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection());

        Assertions.assertNotEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob(), job);
        Assertions.assertEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getEmployer(), "New employer");
        Assertions.assertEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob(), "New job");
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "GP", "MD"})
    public void testUserEditProfileEmploymentForbiddenAccess() throws Exception {
        String employer = this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getEmployer();
        String job = this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob();

        this.mockMvc.perform(post("/users/edit-profile-employment")
                .param("newEmployer" , "")
                .param("newJob", "")
                .with(csrf())
        )
                .andExpect(status().isForbidden());

        Assertions.assertEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getJob(), job);
        Assertions.assertEquals(this.userRepository.findByEmail("firstmail@abv.bg").get()
                .getEmployer(), employer);
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


    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
        dataSetup = new DataSetup(userRepository, userRoleRepository,appointmentRepository);
        dataSetup.BasicDataSetUp();
        dataSetup.AppointmentDataSetup();
        Appointment appointment = this.appointmentRepository.findById(dataSetup.getMockAppointmentId()).get();
        appointment.setStatus(StatusEnum.CONFIRMED);
        appointment = this.appointmentRepository.save(appointment);
        confirmedAppointmentId = appointment.getId();
        gp1Id = dataSetup.getMd1Id();
        gp2Id = dataSetup.getMd2Id();
        userId = dataSetup.getUserId();
    }

}
