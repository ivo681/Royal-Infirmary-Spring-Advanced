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
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testInitialBookAppointmentPage() throws Exception {
        this.mockMvc.perform(get("/appointments/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookappointment"))
                .andExpect(model().attributeExists("gpId"));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testChooseSpecialistPage() throws Exception {
        this.mockMvc.perform(get("/appointments/book/choose-specialist"))
                .andExpect(status().isOk())
                .andExpect(view().name("choose-specialist"))
                .andExpect(model().attributeExists("mds"));
    }
}
