package com.example.webmoduleproject.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIndexPage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
        .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testHomePage() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void testAboutUsPage() throws Exception {
        this.mockMvc.perform(get("/about-us"))
                .andExpect(status().isOk())
                .andExpect(view().name("about-us"));
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testHomePageAccess() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "mandrazhiiski@abv.bg", roles = {"PATIENT", "MD", "GP"})
    public void testAboutUsPageAccess() throws Exception {
        this.mockMvc.perform(get("/about-us"))
                .andExpect(status().isForbidden());
    }

}
