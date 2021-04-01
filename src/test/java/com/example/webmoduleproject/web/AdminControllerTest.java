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
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private DataSetup dataSetup;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAdminMenuPage() throws Exception {
        this.mockMvc.perform(get("/admin/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-nav"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAllPatientsPage() throws Exception {
        this.mockMvc.perform(get("/admin/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminviewlist"))
        .andExpect(model().attribute("patientsView", true))
        .andExpect(model().attributeExists("patients"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAllPersonnelPage() throws Exception {
        this.mockMvc.perform(get("/admin/personnel"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminviewlist"))
                .andExpect(model().attribute("personnelView", true))
                .andExpect(model().attributeExists("mds"));
    }

    @Test
    @WithMockUser(username = "secondmail@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testDailyLogsPage() throws Exception {
        this.mockMvc.perform(get("/admin/daily-logs"))
                .andExpect(status().isOk())
                .andExpect(view().name("dailylogs"))
                .andExpect(model().attributeExists("appointmentsCreated"))
                .andExpect(model().attributeExists("logs"));
    }

    @BeforeEach
    public void setUp() {
        dataSetup = new DataSetup(userRepository, userRoleRepository);
        dataSetup.BasicDataSetUp();
    }
}
