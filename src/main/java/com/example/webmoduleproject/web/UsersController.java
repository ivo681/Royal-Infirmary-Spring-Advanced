package com.example.webmoduleproject.web;


import com.example.webmoduleproject.model.binding.UserRegisterBindingModel;
import com.example.webmoduleproject.model.service.UserRegisterServiceModel;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;

@Controller
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    public UsersController(UserService userService, AppointmentService appointmentService, ModelMapper modelMapper) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
         
    }

    @PostMapping("/login-error")
    public String failedLogin(@ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                                            String username,
                                    RedirectAttributes attributes) {

        attributes.addFlashAttribute("bad_credentials", true);
        attributes.addFlashAttribute("username", username);
        return "redirect:/users/login";
         
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegisterBindingModel")) {
            model.addAttribute("userRegisterBindingModel", new UserRegisterBindingModel());
            model.addAttribute("userExistsError", false);
        }
        return "register";
         
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userRegisterBindingModel") UserRegisterBindingModel userRegisterBindingModel,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors() ||
                (Period.between(userRegisterBindingModel.getDateOfBirth(), LocalDate.now()).getYears() < 18)) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            if (userRegisterBindingModel.getDateOfBirth() != null &&
                    Period.between(userRegisterBindingModel.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
                bindingResult.rejectValue("dateOfBirth", "error.userRegisterBindingModel", "You must be at least 18 years old to register");
            }
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel",
                    bindingResult);
            return "register";
             
        }

        if (this.userService.emailExists(userRegisterBindingModel.getEmail())) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("userExistsError", true);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel",
                    bindingResult);
            return "register";
             
        }

        UserRegisterServiceModel userRegisterServiceModel = this.modelMapper.map(userRegisterBindingModel, UserRegisterServiceModel.class);
        this.userService.registerAndLoginUser(userRegisterServiceModel);
        return "redirect:/complete-profile";
         
    }

    @GetMapping("/change-gp/{id}")
    public String changeGp(@PathVariable("id") String id, Principal principal,
                                 Model model) {
        String userEmail = principal.getName();
        this.userService.setGpById(userEmail, id);
        return "redirect:/home";
         
    }


    @GetMapping("/appointments")
    public String viewPatientDetails(Model model) {
        model.addAttribute("enableMdSection", false);
        model.addAttribute("enablePatientSection", true);
        return "appointment-nav";
         
    }

    @GetMapping("/appointments/past")
    public String viewPatientAppointmentsPast(Principal principal, Model model) {
        String userEmail = principal.getName();
        model.addAttribute("past", true);
        model.addAttribute("currDate", LocalDate.now());
        model.addAttribute("appointments",
                this.appointmentService.
                        getPastAppointmentsForPatient(userEmail));
        return "appointment-list-patients";
         
    }

    @GetMapping("/appointments/future")
    public String viewPatientAppointmentsFuture(Principal principal, Model model) {
        String userEmail = principal.getName();

        model.addAttribute("future", true);
        model.addAttribute("currDate", LocalDate.now());
        model.addAttribute("appointments",
                this.appointmentService.
                        getFutureAppointmentsForPatient(userEmail));
        return "appointment-list-patients";
         
    }

    @GetMapping("/appointments/cancel-{id}")
    public String cancelFutureAppointment(@PathVariable("id") String id,
                                                Model model) {
        if (this.appointmentService.doesAppointmentExistById(id)) {
            this.appointmentService.cancelAppointmentById(id);
        }
        return "redirect:/users/appointments/future";
    }


}


