package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.binding.AppointmentBindingModel;
import com.example.webmoduleproject.model.service.AppointmentServiceModel;
import com.example.webmoduleproject.model.view.appointments.AppointmentConfirmViewModel;
import com.example.webmoduleproject.model.view.MdViewModel;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentsController {
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    public AppointmentsController(UserService userService, AppointmentService appointmentService, ModelMapper modelMapper) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/book")
    public String appointmentMenu(Principal principal, Model model) {
        String userEmail = principal.getName();
        String gpId = this.userService.getGpIdByUserEmail(userEmail);
        model.addAttribute("gpId", gpId);
        return "bookappointment";
    }

    @GetMapping("/book/choose-specialist")
    public String chooseSpecialist(Model model) {
        List<MdViewModel> mds = this.userService.getAllMdsExceptGps();
        model.addAttribute("mds", mds);
        return "choose-specialist";
    }

    @GetMapping("/book/{id}")
    public String appointmentBuild(@PathVariable("id") String id,
                                   Principal principal, Model model) {
        String userEmail = principal.getName();
        String mdFullNameById = this.userService.getMdFullNameById(id);
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("mdId", id);
        model.addAttribute("mdFullName", mdFullNameById);
        if (!model.containsAttribute("appointmentBindingModel")){
            AppointmentBindingModel appointmentBindingModel = new AppointmentBindingModel();
            model.addAttribute("noVacancy" , false);
            model.addAttribute("confirmedAppointment", false);
            model.addAttribute("appointmentBindingModel", appointmentBindingModel);
        }
        return "bookappointmenttwo";
    }

    @PostMapping("/book/{id}")
    public String appointmentCreate(@Valid @ModelAttribute("appointmentBindingModel") AppointmentBindingModel appointmentBindingModel,
                                    BindingResult bindingResult, @PathVariable("id") String id,
                                     Principal principal, @RequestParam(name="md", required=true) String mdName,
                                     RedirectAttributes redirectAttributes) {
        String userEmail = principal.getName();
        boolean availabilityForDateAndTime = this.appointmentService.checkAvailabilityForDateAndTime(appointmentBindingModel.getDate(),
                appointmentBindingModel.getTimeSpan(), mdName);
        boolean alreadyHaveConfirmedAppointment = this.appointmentService.checkForConfirmedAppointment(
                appointmentBindingModel.getDate(), userEmail, id
        );
        if (bindingResult.hasErrors() || !availabilityForDateAndTime ||
        alreadyHaveConfirmedAppointment) {
            if (!availabilityForDateAndTime){
            redirectAttributes.addFlashAttribute("noVacancy" , true);
            }
            if (alreadyHaveConfirmedAppointment){
            redirectAttributes.addFlashAttribute("confirmedAppointment", true);
            }
            redirectAttributes.addFlashAttribute("appointmentBindingModel", appointmentBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.appointmentBindingModel",
                    bindingResult);
            return "redirect:/appointments/book/" + id;
        }
        AppointmentServiceModel appointmentServiceModel = this.modelMapper
                .map(appointmentBindingModel, AppointmentServiceModel.class);
        appointmentServiceModel.setUserEmail(userEmail);
        appointmentServiceModel.setMd_Id(id);
        String newAppointmentId = this.appointmentService.appointmentCreate(appointmentServiceModel);
        return String.format("redirect:/appointments/confirm/%s", newAppointmentId);
    }

    @GetMapping("/confirm/{id}")
    public String appointmentConfirm(@PathVariable("id") String id,
                                   Principal principal, Model model) {
        if (this.appointmentService.isAppointmentUnconfirmed(id)){
            AppointmentConfirmViewModel unconfirmedAppointmentById = this.appointmentService.getUnconfirmedAppointmentById(id);
            unconfirmedAppointmentById.setUserEmail(principal.getName());
            model.addAttribute("unconfirmedAppointmentById", unconfirmedAppointmentById);
            return "appointmentconfirm";
        }
        return "redirect:/patients/all/";
    }

    @PostMapping("/confirm/{id}")
    public String appointmentProcess(@PathVariable("id") String id,
                                     @RequestParam(value="action", required=true) String action,
                                     @RequestParam(name="date", required=true) String dateStr,
                                     @RequestParam(name="timeSpan", required=true) String timeSpan,
                                     @RequestParam(name="md", required=true) String mdName,
                                           Model model){
        LocalDate date = LocalDate.parse(dateStr);
        if (action.equals("cancel")) {
            this.appointmentService.cancelAppointmentById(id);
            return "redirect:/appointments/book";
        } else {
            if (!this.appointmentService.checkAvailabilityForDateAndTime(
                    date , timeSpan, mdName
            )){
                return "redirect:/appointments/book";
            }
            this.appointmentService.confirmAppointmentById(id);
            return "redirect:/home";
        }
    }
}
