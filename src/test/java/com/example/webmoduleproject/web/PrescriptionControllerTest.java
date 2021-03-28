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
public class PrescriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAllPatientPrescriptionsPage() throws Exception {
        this.mockMvc.perform(get("/prescriptions/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", false));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testOwnPrescriptionsPage() throws Exception {
        this.mockMvc.perform(get("/prescriptions/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions-list"))
                .andExpect(model().attribute("own", true));
    }
}
