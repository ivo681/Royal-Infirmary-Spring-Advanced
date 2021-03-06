package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class ProfileCompletionControllerTest {
    private String gpId;
    @Autowired
    private MockMvc mockMvc;
    private UserDetailsService userService;
    private DataSetup dataSetup;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;


    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
        dataSetup = new DataSetup(userRepository, userRoleRepository);
        dataSetup.ProfileCompletionPartialDataSetUp();
        gpId = userRepository.findByEmail("secondmail@abv.bg").get().getId();
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testCompleteProfilePage() throws Exception {
        this.mockMvc.perform(get("/complete-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("complete-profile"))
                .andExpect(model().attributeExists("completeProfileBindingModel"));
    }

    @Test
    @WithMockUser(username = "firstmail@abv.bg", roles = {"PATIENT"})
    public void testCompleteProfileInvalidDataPage() throws Exception {
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
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
    @Transactional
    public void testChooseGpConfirmPage() throws Exception {
        this.mockMvc.perform(patch("/choose-gp/{id}", gpId)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/choose-job"));
        User loggedUser = userRepository.findByEmail("thirdmail@abv.bg").get();
        Assertions.assertEquals(loggedUser.getGp().getId(), gpId);
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
    public void testChooseGpPage() throws Exception {
        User user = userRepository.findByEmail("thirdmail@abv.bg").get();
        user.setGp(null);
        userRepository.saveAndFlush(user);

        this.mockMvc.perform(get("/choose-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("choosegp"))
                .andExpect(model().attributeExists("allGps"));
    }

    @Test
    @WithMockUser(username = "thirdmail@abv.bg", roles = {"PATIENT", "MD"})
    public void testChooseJobPage() throws Exception {
        User user = userRepository.findByEmail("thirdmail@abv.bg").get();
        user.setGp(userRepository.findByHospitalId(1L).get());
        userRepository.saveAndFlush(user);

        this.mockMvc.perform(get("/choose-job"))
                .andExpect(status().isOk())
                .andExpect(view().name("choose-job"))
                .andExpect(model().attributeExists("jobs"));
    }


    @Test
    @WithMockUser(username = "fourthmail@abv.bg", roles = {"MD", "PATIENT"})
    @Transactional
    public void testChooseJobConfirmPage() throws Exception {
        User user = userRepository.findByEmail("fourthmail@abv.bg").get();
        String position = "Surgeon";
        Assertions.assertNull(user.getJob());

        this.mockMvc.perform(patch("/choose-job/{job}", position)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"));
    }
}
