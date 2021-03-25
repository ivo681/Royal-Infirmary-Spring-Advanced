package com.example.webmoduleproject.init;

import com.example.webmoduleproject.service.UserRoleService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements CommandLineRunner {
    private final UserRoleService userRoleService;
    private final UserService userService;

    public DataInit(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.userRoleService.seedRoles();
        this.userService.seedGps();
        this.userService.seedMds();
        this.userService.seedPatients();
    }
}
