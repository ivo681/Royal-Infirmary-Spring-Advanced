package com.example.webmoduleproject.web;


import com.example.webmoduleproject.exceptions.NotFoundError;
import com.example.webmoduleproject.exceptions.PermissionError;
import com.example.webmoduleproject.model.binding.ContactDetailsBindingModel;
import com.example.webmoduleproject.model.binding.EmploymentDetailsBindingModel;
import com.example.webmoduleproject.model.binding.UserRegisterBindingModel;
import com.example.webmoduleproject.model.service.ContactDetailsServiceModel;
import com.example.webmoduleproject.model.service.EmploymentDetailsServiceModel;
import com.example.webmoduleproject.model.service.UserRegisterServiceModel;
import com.example.webmoduleproject.model.view.GpViewModel;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public String login(HttpServletRequest request, Model model) {
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
            bindingResult.rejectValue("email", "userExistsError", "This email is already used by another user");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel",
                    bindingResult);
            return "register";

        }

        UserRegisterServiceModel userRegisterServiceModel = this.modelMapper.map(userRegisterBindingModel, UserRegisterServiceModel.class);
        this.userService.registerAndLoginUser(userRegisterServiceModel);
        return "redirect:/complete-profile";

    }

    @PatchMapping("/change-gp/{id}")
    public String changeGp(@PathVariable("id") String id, Principal principal) {
        String userEmail = principal.getName();
        this.userService.setGpById(userEmail, id);
        return "redirect:/home";

    }

    @GetMapping("/change-gp")
    public String changeGpPage(Principal principal, Model model) {
        String userEmail = principal.getName();
        if (!this.userService.hasTelephone(userEmail)) {
            return "redirect:/complete-profile";
        }
        if (!this.userService.hasGp(userEmail)) {
            return "redirect:/choose-gp";
        }
        String gpIdByUserEmail = this.userService.getGpIdByUserEmail(userEmail);
        List<GpViewModel> allGps = this.userService.getAllGpsExcept(gpIdByUserEmail, userEmail);
        model.addAttribute("allGps", allGps);
        model.addAttribute("changing", true);
        return "choosegp";
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

    @DeleteMapping("/appointments/cancel-{id}")
    public String cancelFutureAppointment(@PathVariable("id") String id,
                                          Principal principal) throws NotFoundError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistByIdAndPatientEmail(id, userEmail)) {
            this.appointmentService.cancelAppointmentById(id);
            return "redirect:/users/appointments/future";
        }
        throw new NotFoundError("Appointment not found with this id and patient email");
    }


    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_GP')")
    public String getUserDetailsForAdmin(@PathVariable("id") String id,
                                         Model model) {
        model.addAttribute("person", this.userService.getPatientDetails(id));
        return "person-details";
    }

    @GetMapping("/edit-profile")
    public String loadEditProfilePage(@ModelAttribute("contactDetailsBindingModel") ContactDetailsBindingModel contactDetailsBindingModel,
                                      @ModelAttribute("employmentDetailsBindingModel") EmploymentDetailsBindingModel employmentDetailsBindingModel,
                                      Principal principal,
                                      Model model, HttpServletRequest request) {
        String userEmail = principal.getName();
        if (this.userService.hasTelephone(userEmail)) {
            Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
            if (inputFlashMap != null) {
                if (inputFlashMap.containsKey("contactErrors")) {
                    model.addAttribute("contactErrors", (List<String>) inputFlashMap.get("contactErrors"));
                } else {
                    model.addAttribute("employmentErrors", (List<String>) inputFlashMap.get("employmentErrors"));
                }
            }
            if (!model.containsAttribute("person")) {
                model.addAttribute("person", this.userService.getPatientDetailsByEmail(userEmail));
            }
            if (!model.containsAttribute("employmentDetailsBindingModel") || employmentDetailsBindingModel == null) {
                model.addAttribute("employmentDetailsBindingModel", new EmploymentDetailsBindingModel());
            }
            if (!model.containsAttribute("contactDetailsBindingModel") || contactDetailsBindingModel == null) {
                model.addAttribute("contactDetailsBindingModel", new ContactDetailsBindingModel());
            }
            return "edit-person-details";
        }
        return "redirect:/home";
    }


    @PatchMapping("/edit-profile-contacts")
    public RedirectView changeUserContactDetails(@Valid @ModelAttribute("contactDetailsBindingModel")
                                                             ContactDetailsBindingModel contactDetailsBindingModel,
                                                     BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                                     Principal principal, Model model
    ) {
        String userEmail = principal.getName();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("contactDetailsBindingModel", contactDetailsBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactDetailsBindingModel",
                    bindingResult);
            List<String> errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            redirectAttributes.addFlashAttribute("person", this.userService.getPatientDetailsByEmail(userEmail));
            redirectAttributes.addFlashAttribute("contactErrors", errorMessages);
            return new RedirectView("/users/edit-profile", true);
        }
        this.userService.changeContactDetails(userEmail, this.modelMapper.map(contactDetailsBindingModel,
                ContactDetailsServiceModel.class));
        //redirectAttributes.addFlashAttribute("person", this.userService.getPatientDetailsByEmail(userEmail));
        return new RedirectView("/home");
    }

    @PatchMapping("/edit-profile-employment")
    public RedirectView changeUserEmploymentDetails(@Valid @ModelAttribute("employmentDetailsBindingModel")
                                                                EmploymentDetailsBindingModel employmentDetailsBindingModel,
                                                        BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                                        Principal principal
    ) throws PermissionError {
        String userEmail = principal.getName();
        if (!this.userService.isUserEmployedInHospital(userEmail)) {
            if ((employmentDetailsBindingModel.getNewEmployer().trim().isBlank() &&
                    !employmentDetailsBindingModel.getNewJob().trim().isBlank()) ||
                    (!employmentDetailsBindingModel.getNewEmployer().trim().isBlank() &&
                            employmentDetailsBindingModel.getNewJob().trim().isBlank())
            ) {
                redirectAttributes.addFlashAttribute("employmentDetailsBindingModel", employmentDetailsBindingModel);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employmentDetailsBindingModel",
                        bindingResult);
                List<String> errorMessages = List.of("You can't submit only one field");
                redirectAttributes.addFlashAttribute("person", this.userService.getPatientDetailsByEmail(userEmail));
                redirectAttributes.addFlashAttribute("employmentErrors", errorMessages);
                return new RedirectView("/users/edit-profile", true);
            }
            this.userService.changeEmploymentDetails(userEmail, this.modelMapper.map(employmentDetailsBindingModel,
                    EmploymentDetailsServiceModel.class));
            redirectAttributes.addFlashAttribute("person", this.userService.getPatientDetailsByEmail(userEmail));
            return new RedirectView("/home");
        }
        throw new PermissionError("Hospital staff are not allowed to make amendments on employment details");
    }

}


