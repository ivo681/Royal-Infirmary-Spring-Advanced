package com.example.webmoduleproject.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
public class SickLeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAllPatientSickLeavesPage() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/own"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", true));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testOwnSickLeavesPage() throws Exception {
        this.mockMvc.perform(get("/sick-leaves/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick-leaves-list"))
                .andExpect(model().attribute("own", false));
    }
}
