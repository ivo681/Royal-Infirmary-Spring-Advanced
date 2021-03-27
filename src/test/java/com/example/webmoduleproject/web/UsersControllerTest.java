package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private User user1;
    private User user2;
    private UserRepository userRepository;

//    private String fUserId = "first1", sUserId= "second2";
//    private String fUserFName = "Shisho", sUserFName = "Misho";
//    private String pass = "TopSecret12!";
//    private String fUserLName = "Bakshisho", sUserLName = "Bakshishov";
//    private LocalDate birthDate = LocalDate.of(1990, 10, 10);
//    private String fUserTel = "1111122222", sUserTel = "3333344444";
//    private String fUserAddress = "Address1", sUserAddress = "Address2";
//    private String fUserEmail = "email1@abv.bg", sUserEmail = "email2@abv.bg";
//    private String fUserIdNumber = "1212121212", sUserIdNumber = "3434343434";


//    @BeforeAll
//    public void init(){
//
//        this.user1 = new User(){};
//        user1.setFirstName(fUserFName);
//        user1.setLastName(fUserLName);
//        user1.setAddress(fUserAddress);
//        user1.setTelephone(fUserTel);
//        user1.setPassword(pass);
//        user1.setIdNumber(fUserIdNumber);
//        user1.setId(fUserId);
//        user1.setDateOfBirth(birthDate);
//        user1.setEmail(fUserEmail);
//
//        this.user2 = new User(){};
//        user2.setFirstName(sUserFName);
//        user2.setLastName(sUserLName);
//        user2.setAddress(sUserAddress);
//        user2.setTelephone(sUserTel);
//        user2.setPassword(pass);
//        user2.setIdNumber(sUserIdNumber);
//        user2.setId(sUserId);
//        user2.setDateOfBirth(birthDate);
//        user2.setEmail(sUserEmail);
//
//        this.userRepository = Mockito.mock(UserRepository.class);
//        this.userRepository.save(user1);
//        this.userRepository.save(user2);
//    }
//

    @Test
    public void testRegisterPage() throws Exception {
        this.mockMvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testLoginPage() throws Exception {
        this.mockMvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

}
