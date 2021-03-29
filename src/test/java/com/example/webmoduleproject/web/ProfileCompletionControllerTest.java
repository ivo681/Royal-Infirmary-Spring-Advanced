package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.UserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:hsqldb:mem:${random.uuid}"
//})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProfileCompletionControllerTest {

    private String gpId;
    @Autowired
    private MockMvc mockMvc;
    private UserDetailsService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;


    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
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

        User gp = new User();
        gp.setFirstName("Misho");
        gp.setLastName("Shisho");
        gp.setEmail("secondmail@abv.bg");
        gp.setDateOfBirth(LocalDate.of(1990, 10, 10));
        gp.setId("2");
        gp.setPassword("TopSecret123!");
        gp.setHospitalId(1L);
        gp.setJob("General Practitioner");
        gp.setTelephone("0888888888");
        gp.setAddress("Sample address");
        gp.setIdNumber("9010100000");
        gp.setRoles(List.of(mockPatient, mockGp, mockMd));
        gp = userRepository.save(gp);
        gpId = gp.getId();

        User md = new User();
        md.setFirstName("Misho");
        md.setLastName("Shisho");
        md.setEmail("thirdmail@abv.bg");
        md.setDateOfBirth(LocalDate.of(1990, 10, 10));
        md.setId("2");
        md.setPassword("TopSecret123!");
        md.setHospitalId(3L);
        md.setTelephone("0888888885");
        md.setAddress("Sample address");
        md.setIdNumber("9010100001");
        md.setRoles(List.of(mockPatient, mockMd));
        md = userRepository.save(md);


        User user1 = new User();
        user1.setFirstName("Shisho");
        user1.setLastName("Bakshisho");
        user1.setEmail("firstmail@abv.bg");
        user1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        user1.setId("3");
        user1.setPassword("TopSecret123!");
//        user1.setTelephone("0888888888");
//        user1.setAddress("Sample address");
//        user1.setIdNumber("9010100000");
//        user1.setGp(md);
        user1.setRoles(List.of(mockPatient));
        user1 = userRepository.save(user1);

    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    //@Order(1)
    public void testCompleteProfilePage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");

        this.mockMvc.perform(get("/complete-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("complete-profile"))
                .andExpect(model().attributeExists("completeProfileBindingModel"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
//    @Order(2)
    public void testCompleteProfileInvalidDataPage() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");
        this.mockMvc.perform(post("/complete-profile")
                .param("idNumber", "")
                .param("telephone", "333")
                .param("address", "Sofia")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/complete-profile"));
        User loggedUser = userRepository.findByEmail("firstmail@abv.bg").get();
        Assertions.assertNull(loggedUser.getIdNumber());
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
//    @Order(3)
    public void testCompleteProfileValidData() throws Exception {
        userService.loadUserByUsername("firstmail@abv.bg");

        this.mockMvc.perform(post("/complete-profile")
                .param("idNumber", "9010100001")
                .param("telephone", "3333444422")
                .param("address", "Sofia")
                .param("job", "")
                .param("employer", "")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/choose-gp"));
        User loggedUser = userRepository.findByEmail("firstmail@abv.bg").get();
        Assertions.assertEquals(loggedUser.getIdNumber(), "9010100001");
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
//    @Order(5)
    @Transactional
    public void testChooseGpConfirmPage() throws Exception {
        userService.loadUserByUsername("thirdmail@abv.bg");

        this.mockMvc.perform(get("/choose-gp/{id}", gpId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/choose-job"));
        User loggedUser = userRepository.findByEmail("thirdmail@abv.bg").get();
        Assertions.assertEquals(loggedUser.getGp().getId(), gpId);
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
//    @Order(4)
    public void testChooseGpPage() throws Exception {
        User user = userRepository.findByEmail("thirdmail@abv.bg").get();
        user.setGp(null);
        userRepository.saveAndFlush(user);
        userService.loadUserByUsername("thirdmail@abv.bg");
        System.out.println(userRepository.findByEmail("thirdmail@abv.bg").get().getGp() == null);

        this.mockMvc.perform(get("/choose-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("choosegp"))
                .andExpect(model().attributeExists("allGps"));
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
//    @Order(6)
    public void testChooseJobPage() throws Exception {
        User user = userRepository.findByEmail("thirdmail@abv.bg").get();
        user.setGp(userRepository.findByHospitalId(1L).get());
        userRepository.saveAndFlush(user);
        userService.loadUserByUsername("thirdmail@abv.bg");

        this.mockMvc.perform(get("/choose-job"))
                .andExpect(status().isOk())
                .andExpect(view().name("choose-job"))
                .andExpect(model().attributeExists("jobs"));
    }


    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
//    @Order(7)
    @Transactional
    public void testChooseJobConfirmPage() throws Exception {
        User user = userRepository.findByEmail("thirdmail@abv.bg").get();
        user.setGp(userRepository.findByHospitalId(1L).get());
        userRepository.saveAndFlush(user);
        userService.loadUserByUsername("thirdmail@abv.bg");
        String position = "General Practitioner";

        this.mockMvc.perform(get("/choose-job/{job}", position))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"));
        User loggedUser = userRepository.findByEmail("thirdmail@abv.bg").get();
        Assertions.assertEquals(loggedUser.getJob(), position);
    }
}
