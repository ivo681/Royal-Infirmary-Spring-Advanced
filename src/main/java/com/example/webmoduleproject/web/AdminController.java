package com.example.webmoduleproject.web;

import com.example.webmoduleproject.service.LogService;
import com.example.webmoduleproject.service.UserService;
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

    public AdminController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
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

    @GetMapping("/daily-logs")
    public String viewDailyLogs(Model model){
        model.addAttribute("logs", this.logService.getLogList());
        model.addAttribute("appointmentsCreated", this.logService.countByAction("appointmentCreate"));
        model.addAttribute("appointmentsConfirmed", this.logService.countByAction("confirmAppointmentById"));
        model.addAttribute("appointmentsCancelled", this.logService.countByAction("cancelFutureAppointment"));
        model.addAttribute("ambulatoryListsCreated", this.logService.countByAction("createNewList"));
        model.addAttribute("prescriptionsCreated", this.logService.countByAction("createNewPrescription"));
        model.addAttribute("sickLeavesCreated", this.logService.countByAction("createNewSickLeave"));
        return "dailylogs";
    }
}
