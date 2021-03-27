package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.impl.UserRoleServiceImpl;
import com.example.webmoduleproject.service.impl.UserServiceImpl;
import com.google.gson.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserServiceTest {
//    private final static String MDS_PATH = "src/main/resources/static/json/medics.json";
//    private final static String PATIENTS_PATH = "src/main/resources/static/json/patients.json";
//    private final static String GPS_PATH = "src/main/resources/static/json/gps.json";
    private UserServiceImpl userService;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    UserRoleServiceImpl userRoleService;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    ModelMapper modelMapper;
    @MockBean
    Gson gson;
    @Mock
    UserDetailsService userDetailsService;
    @MockBean
    Random random;

    private String fUserId = "first1", sUserId= "second2";
    private String fUserFName = "Shisho", sUserFName = "Misho";
    private String pass = "TopSecret12!";
    private String fUserLName = "Bakshisho", sUserLName = "Bakshishov";
    private LocalDate birthDate = LocalDate.of(1990, 10, 10);
    private String fUserTel = "1111122222", sUserTel = "3333344444";
    private String fUserAddress = "Address1", sUserAddress = "Address2";
    private String fUserEmail = "email1@abv.bg", sUserEmail = "email2@abv.bg";
    private String fUserIdNumber = "1212121212", sUserIdNumber = "3434343434";


    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        userService = new UserServiceImpl(userRoleRepository,
        userRepository, passwordEncoder, modelMapper,gson,
        userDetailsService, random);

        userRoleService = new UserRoleServiceImpl(userRoleRepository);

        UserRole mockAdmin = new UserRole();
        mockAdmin.setRole(RoleEnum.ADMIN);
        UserRole mockPatient = new UserRole();
        mockPatient.setRole(RoleEnum.PATIENT);
        UserRole mockGp = new UserRole();
        mockGp.setRole(RoleEnum.GP);
        UserRole mockMd = new UserRole();
        mockMd.setRole(RoleEnum.MD);
//        when(userRoleRepository.save(any(UserRole.class))).thenReturn(mockAdmin);
        userRoleRepository.save(mockAdmin);

//        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class))).
//                thenAnswer(i -> i.getArguments()[0]);
//
//        userRoleRepository.saveAll(List.of(mockMd, mockPatient, mockAdmin, mockGp));

    }

//    @Test
//    public void testSeedAllGps() throws IOException {
//        System.out.println(userRoleRepository.count());
//        userService.seedGps();
////
////        Assertions.assertEquals(8, userRepository.count());
//    }

//    @Test
//    public void testRegisterUser(){
//        User user1 = new User();
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
//        userService.re
//    }
}
