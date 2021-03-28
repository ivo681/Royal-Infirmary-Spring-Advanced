package com.example.webmoduleproject.web;

import com.example.webmoduleproject.service.LogService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final LogService logService;
    private final ModelMapper modelMapper;

    public AdminController(UserService userService, LogService logService, ModelMapper modelMapper) {
        this.userService = userService;
        this.logService = logService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/menu")
    public String adminNavigationMenu(){
        return "admin-nav";
    }

    @GetMapping("/patients")
    public String viewAllHospitalPatients(Model model){
        model.addAttribute("patientsView", true);
        model.addAttribute("patients", this.userService.getPatientList());
        return "adminviewlist";
    }

    @GetMapping("/personnel")
    public String viewAllHospitalPersonnel(Model model){
        model.addAttribute("personnelView", true);
        model.addAttribute("mds", this.userService.getPersonnelList());
        return "adminviewlist";
    }
}
