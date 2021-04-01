package com.example.webmoduleproject.init;

import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserRoleService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.rmi.ServerError;

@Component
public class DataInit implements CommandLineRunner {
    private final UserRoleService userRoleService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    public DataInit(UserRoleService userRoleService, UserService userService, AppointmentService appointmentService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.userRoleService.seedRoles();
        this.userService.seedGps();
        this.userService.seedMds();
        this.userService.seedPatients();
        this.appointmentService.seedAppointments();

    }
}
