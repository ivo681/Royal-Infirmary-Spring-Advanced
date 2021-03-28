package com.example.webmoduleproject.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAdminMenuPage() throws Exception {
        this.mockMvc.perform(get("/admin/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-nav"));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAllPatientsPage() throws Exception {
        this.mockMvc.perform(get("/admin/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminviewlist"))
        .andExpect(model().attribute("patientsView", true))
        .andExpect(model().attributeExists("patients"));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP", "ADMIN"})
    public void testAllPersonnelPage() throws Exception {
        this.mockMvc.perform(get("/admin/personnel"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminviewlist"))
                .andExpect(model().attribute("personnelView", true))
                .andExpect(model().attributeExists("mds"));
    }
}
