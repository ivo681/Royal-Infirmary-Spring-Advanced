package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void seedRoles() {
        if (this.userRoleRepository.count() == 0){
            Arrays.stream(RoleEnum.values()).forEach(e -> {
                UserRole userRole = new UserRole();
                userRole.setRole(e);
                this.userRoleRepository.save(userRole);
            });
        }
    }
}
