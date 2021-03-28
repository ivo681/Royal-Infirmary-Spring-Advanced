package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    private User user1;
    User user2;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserDetailsService(userRepository);
        UserRole mockAdmin = new UserRole();
        mockAdmin.setRole(RoleEnum.ADMIN);
        UserRole mockPatient = new UserRole();
        mockPatient.setRole(RoleEnum.PATIENT);
        UserRole mockGp = new UserRole();
        mockGp.setRole(RoleEnum.GP);
        UserRole mockMd = new UserRole();
        mockMd.setRole(RoleEnum.MD);

        user1 = new User();
        user1.setFirstName("Shisho");
        user1.setLastName("Bakshisho");
        user1.setEmail("firstmail@abv.bg");
        user1.setDateOfBirth(LocalDate.of(1990, 10, 10));
        user1.setId("1");
        user1.setPassword("TopSecret123!");

        user1.setRoles(List.of(mockAdmin, mockPatient));
//        this.userRepository.save(user1);
    }


    @Test
    void testUserNotFound() {
        Assertions.assertThrows(
                UsernameNotFoundException.class, () -> {
                    userService.loadUserByUsername("user1");
                });
    }

    @Test
    void testExistingUser() {
        System.out.println(this.userRepository.count());
        System.out.println(user1.getEmail());

        Mockito.when(userRepository.findByEmail("firstmail@abv.bg"))
                .thenReturn(Optional.of(user1));

        UserDetails userDetails = userService.loadUserByUsername("firstmail@abv.bg");

        Assertions.assertEquals(user1.getEmail(), userDetails.getUsername());
        Assertions.assertEquals(2, userDetails.getAuthorities().size());
        List<String> authorities = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).collect(Collectors.toList());

        Assertions.assertTrue(authorities.contains("ROLE_PATIENT"));
    }
}
