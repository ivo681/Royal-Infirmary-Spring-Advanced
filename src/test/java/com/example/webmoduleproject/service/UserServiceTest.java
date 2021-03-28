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
import org.modelmapper.internal.util.Assert;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    //    private final static String MDS_PATH = "src/main/resources/static/json/medics.json";
//    private final static String PATIENTS_PATH = "src/main/resources/static/json/patients.json";
//    private final static String GPS_PATH = "src/main/resources/static/json/gps.json";
    private UserDetailsService userService;
    @Mock
    UserRoleRepository userRoleRepository;

    private String fUserId = "first1", sUserId = "second2";
    private String fUserFName = "Shisho", sUserFName = "Misho";
    private String pass = "TopSecret12!";
    private String fUserLName = "Bakshisho", sUserLName = "Bakshishov";
    private LocalDate birthDate = LocalDate.of(1990, 10, 10);
    private String fUserEmail = "email1@abv.bg", sUserEmail = "email2@abv.bg";


    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);

//        UserRole mockAdmin = new UserRole();
//        mockAdmin.setRole(RoleEnum.ADMIN);
//        UserRole mockPatient = new UserRole();
//        mockPatient.setRole(RoleEnum.PATIENT);
//        UserRole mockGp = new UserRole();
//        mockGp.setRole(RoleEnum.GP);
//        UserRole mockMd = new UserRole();
//        mockMd.setRole(RoleEnum.MD);
//        when(userRoleRepository.save(any(UserRole.class))).thenReturn(mockAdmin);
//        userRoleRepository.save(mockAdmin);

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

    @Test
    void testUserNotFound() {
        Assertions.assertThrows(
                UsernameNotFoundException.class, () -> {
                    userService.loadUserByUsername("user1");
                });
    }

    @Test
    void testExistingUser() {
        User user = new User();
        user.setFirstName(fUserFName);
        user.setLastName(fUserLName);
        user.setEmail(fUserEmail);
        user.setDateOfBirth(birthDate);
        user.setId(fUserId);
        user.setPassword(pass);

        UserRole mockPatient = new UserRole();
        mockPatient.setRole(RoleEnum.PATIENT);
        UserRole mockAdmin = new UserRole();
        mockAdmin.setRole(RoleEnum.ADMIN);

        user.setRoles(List.of(mockAdmin, mockPatient));

        Mockito.when(userRepository.findByEmail(fUserEmail))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("email1@abv.bg");

        Assertions.assertEquals(user.getEmail(), userDetails.getUsername());
        Assertions.assertEquals(2, userDetails.getAuthorities().size());
        List<String> authorities = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).collect(Collectors.toList());

        Assertions.assertTrue(authorities.contains("ROLE_PATIENT"));
    }
}
