package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.binding.AmbulatoryListBindingModel;
import com.example.webmoduleproject.model.view.AmbulatoryListAllViewModel;
import com.example.webmoduleproject.model.view.AmbulatoryListViewModel;
import com.example.webmoduleproject.model.view.MdDocumentViewModel;
import com.example.webmoduleproject.model.view.PatientAmbulatoryListViewModel;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.PrescriptionService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/ambulatory-list")
public class AmbulatoryListController {
    private final UserService userService;
    private final AmbulatoryListService ambulatoryListService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    public AmbulatoryListController(UserService userService, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, PrescriptionService prescriptionService) {
        this.userService = userService;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/new/{id}")
    public String ambulatoryListBuild(@PathVariable("id") String id,
                                            Principal principal, Model model) {
        String userEmail = principal.getName();
        if (this.userService.hasMdRole(userEmail)) {
            if (this.appointmentService.doesAppointmentExistById(id)) {
                if (!this.ambulatoryListService.existingListForAppointment(id)) {
                    if (!model.containsAttribute("ambulatoryListBindingModel")) {
                        model.addAttribute("ambulatoryListBindingModel", new AmbulatoryListBindingModel());
                    }
                    MdDocumentViewModel mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(id);
                    PatientAmbulatoryListViewModel patientViewModelByAppointmentId = this.appointmentService.getPatientViewModelByAppointmentId(id);
                    model.addAttribute("mdViewModel", mdDetailsByAppointmentId);
                    model.addAttribute("patientViewModel", patientViewModelByAppointmentId);
                    model.addAttribute("appId", id);
                    model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    return "ambulatory";
                }
                return "redirect:/ambulatory-list/details/" + id;
            }
            return "redirect:/error";
        }
        return "redirect:/home";
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/create-{id}")
    public String ambulatoryListCreate(@Valid @ModelAttribute("ambulatoryListBindingModel") AmbulatoryListBindingModel
                                                     ambulatoryListBindingModel,
                                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                             @PathVariable("id") String appointmentId) {
        if (this.appointmentService.doesAppointmentExistById(appointmentId)) {
            if (!this.ambulatoryListService.existingListForAppointment(appointmentId)) {
                if (bindingResult.hasErrors()) {
                    redirectAttributes.addFlashAttribute("ambulatoryListBindingModel", ambulatoryListBindingModel);
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ambulatoryListBindingModel",
                            bindingResult);
                    return "redirect:/ambulatory-list/new/" + appointmentId;
                }
                this.ambulatoryListService.createNewList(appointmentId, ambulatoryListBindingModel);
                if (!ambulatoryListBindingModel.getMedicines().trim().isBlank()) {
                    this.prescriptionService.createNewPrescription(appointmentId, ambulatoryListBindingModel.getMedicines());
                }
            }
            return "redirect:/ambulatory-list/details/" + appointmentId;
             //make it return the confirmed ambulatory list
        }
        return "redirect:/error";
    }

    @GetMapping("/details/{id}")
    public String getExistingAmbulatoryList(@PathVariable("id") String id, Model model) {
        if (this.ambulatoryListService.existingListForAppointment(id)) {
            AmbulatoryListViewModel ambulatoryListByAppointmentId = this.ambulatoryListService.getAmbulatoryListByAppointmentId(id);
            MdDocumentViewModel mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(id);
            mdDetailsByAppointmentId.setTelephone(ambulatoryListByAppointmentId.getMdTelephoneNumber());
            PatientAmbulatoryListViewModel patientViewModelByAppointmentId = this.appointmentService.getPatientViewModelByAppointmentId(id);
            patientViewModelByAppointmentId.setTelephone(ambulatoryListByAppointmentId.getPatientTelephoneNumber());
            model.addAttribute("mdViewModel", mdDetailsByAppointmentId);
            model.addAttribute("patientViewModel", patientViewModelByAppointmentId);
            model.addAttribute("ambulatoryList", ambulatoryListByAppointmentId);
            model.addAttribute("patientEmployed", this.userService.isPatientEmployedByEmail(patientViewModelByAppointmentId.getEmail()));
            model.addAttribute("givePrescription", !ambulatoryListByAppointmentId.getMedicines().isBlank());
            model.addAttribute("showButtons", LocalDate.now().equals(ambulatoryListByAppointmentId.getDate()));
            return "ambulatory-confirm";
        }
        return "redirect:/error";
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/all")
    public String getAllAmbulatoryListsByMd(Principal principal, Model model) {
        String userEmail = principal.getName();
        List<AmbulatoryListAllViewModel> lists = this.ambulatoryListService.getAllListsByMdName(userEmail);
        model.addAttribute("own", false);
        model.addAttribute("lists", lists);
        return "ambulatory-lists";
    }

    @GetMapping("/own")
    public String getOwnAmbulatoryListsByUserEmail(Principal principal,
                                                   Model model) {
        String userEmail = principal.getName();
        List<AmbulatoryListAllViewModel> lists = this.ambulatoryListService.getAllListsByPatientEmail(userEmail);
        model.addAttribute("own", true);
        model.addAttribute("lists", lists);
        return "ambulatory-lists";
    }
}


