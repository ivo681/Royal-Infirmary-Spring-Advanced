package com.example.webmoduleproject.web;

import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;


@Controller
@PreAuthorize("hasRole('ROLE_MD')")
@RequestMapping("/patients")
public class PatientsController {
    private final UserService userService;
    private final AppointmentService appointmentService;

    public PatientsController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('ROLE_GP')")
    @GetMapping("/all")
    public String viewMyPatientsList(Model model, Principal principal) {
        String userEmail = principal.getName();
        model.addAttribute("patients", this.userService.getPatientListByGpEmail(userEmail));
        return "patientslist";
    }

    @PreAuthorize("hasRole('ROLE_GP')")
    @GetMapping("/details/{id}")
    public String viewPatientDetails(@PathVariable("id") String id,
                                      Model model) {
        model.addAttribute("person", this.userService.getPatientDetails(id));
        return "person-details";
    }

    @GetMapping("/appointments")
    public String viewAppointmentNavigation(Model model) {
        model.addAttribute("enableMdSection", true);
        return "appointment-nav";
    }

    @GetMapping("/appointments/today")
    public String viewPatientAppointmentsToday(Principal principal, Model model) {
        String userEmail = principal.getName();
        model.addAttribute("today", true);
        model.addAttribute("appointments", this.appointmentService.
                getTodaysAppointmentsForDoctor(userEmail));
        return "appointment-list";
    }

    @GetMapping("/appointments/past")
    public String viewPatientAppointmentsPast(Principal principal, Model model) {
        String userEmail = principal.getName();
        model.addAttribute("past", true);
        model.addAttribute("appointments",
                this.appointmentService.
                        getPastAppointmentsForDoctor(userEmail));
        return "appointment-list";
    }

    @GetMapping("/appointments/future")
    public String viewPatientAppointmentsFuture(Principal principal, Model model) {
        String userEmail = principal.getName();
        model.addAttribute("future", true);
        model.addAttribute("appointments",
                this.appointmentService.
                        getFutureAppointmentsForDoctor(userEmail));
        return "appointment-list";
    }
}
