package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.binding.SickLeaveBindingModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientSickLeaveDetails;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveViewModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveListAllViewModel;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.SickLeaveService;
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
@RequestMapping("/sick-leaves/")
public class SickLeaveController {
    private final UserService userService;
    private final AmbulatoryListService ambulatoryListService;
    private final AppointmentService appointmentService;
    private final SickLeaveService sickLeaveService;

    public SickLeaveController(UserService userService, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, SickLeaveService sickLeaveService) {
        this.userService = userService;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.sickLeaveService = sickLeaveService;
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/new/{id}")
    public String sickLeaveBuildByAppointmentId(@PathVariable("id") String appointmentId,
                                                      Principal principal, Model model) {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistById(appointmentId) && this.ambulatoryListService.existingListForAppointment(appointmentId)) {
            if (!this.sickLeaveService.existingSickLeaveByAppointmentIdMdCheck(appointmentId, userEmail)) {
                if (!model.containsAttribute("sickLeaveBindingModel")) {
                    model.addAttribute("sickLeaveBindingModel", new SickLeaveBindingModel());
                }
                MdDocumentDetails mdViewModel = this.appointmentService.getMdDetailsByAppointmentId(appointmentId);
                PatientSickLeaveDetails patientViewModel = this.appointmentService.getSickPatientViewModelByAppointmentId(appointmentId);
                model.addAttribute("mdViewModel", mdViewModel);
                patientViewModel.setDiagnosis(this.ambulatoryListService.getDiagnosisFromListByAppointmentId(appointmentId));
                model.addAttribute("patientViewModel", patientViewModel);
                model.addAttribute("appId", appointmentId);
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "sick-leave";
                
            }
            return "redirect:/sick-leaves/details/" + appointmentId;
            
        }
        return "redirect:/error";
        
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/create-{id}")
    public String ambulatoryListCreate(@Valid @ModelAttribute("sickLeaveBindingModel") SickLeaveBindingModel
                                                     sickLeaveBindingModel,
                                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                             @PathVariable("id") String appointmentId,
                                             Principal principal) {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistById(appointmentId)
                && this.ambulatoryListService.existingListForAppointment(appointmentId)) {
            if (!this.sickLeaveService.existingSickLeaveByAppointmentIdMdCheck(appointmentId, userEmail)) {
                if (bindingResult.hasErrors() || sickLeaveBindingModel.getFromDate() == null) {
                    if (!sickLeaveBindingModel.getFromDate().equals(LocalDate.now())) {
                        bindingResult.rejectValue("fromDate", "error.sickLeaveBindingModel", "The start date must be today");
                    }
                    redirectAttributes.addFlashAttribute("sickLeaveBindingModel", sickLeaveBindingModel);
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.sickLeaveBindingModel",
                            bindingResult);
                    return "redirect:/sick-leaves/new/" + appointmentId;
                    
                }
                this.sickLeaveService.createNewSickLeave(appointmentId, sickLeaveBindingModel);
            }
            return "redirect:/sick-leaves/details/" + appointmentId;
            
        }
        return "redirect:/error";
        
    }

    @GetMapping("/details/{id}")
    public String ambulatoryListCreate(@PathVariable("id") String appointmentId,
                                             Model model) {
        if (this.sickLeaveService.existingSickLeaveByAppointmentId(appointmentId)) {
            SickLeaveViewModel sickLeaveView = this.sickLeaveService.getSickLeaveByAppointmentId(appointmentId);
            MdDocumentDetails mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(appointmentId);
            mdDetailsByAppointmentId.setTelephone(sickLeaveView.getMdTelephoneNumber());
            PatientSickLeaveDetails patientSickLeaveDetails = this.appointmentService.
                    getSickPatientViewModelByAppointmentId(appointmentId);
            patientSickLeaveDetails.setTelephone(sickLeaveView.getPatientTelephoneNumber());
            patientSickLeaveDetails.setJob(sickLeaveView.getPatientJob());
            patientSickLeaveDetails.setEmployer(sickLeaveView.getPatientEmployer());
            patientSickLeaveDetails.setAddress(sickLeaveView.getPatientAddress());
            model.addAttribute("sickLeaveView", sickLeaveView);
            model.addAttribute("patientView", patientSickLeaveDetails);
            model.addAttribute("mdView", mdDetailsByAppointmentId);
            return "sick-leave-confirm";
            
        }
        return "redirect:/error";
        
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/all")
    public String getAllSickLeaves(Principal principal, Model model) {
        String userEmail = principal.getName();
        List<SickLeaveListAllViewModel> sickLeaves = this.sickLeaveService.getAllSickLeavesByMdName(userEmail);
        model.addAttribute("own", false);
        model.addAttribute("sickLeaves", sickLeaves);
        return "sick-leaves-list";
        
    }

    @GetMapping("/own")
    public String getOwnSickLeaves(Principal principal, Model model) {
        String userEmail = principal.getName();
        List<SickLeaveListAllViewModel> sickLeaves = this.sickLeaveService.getOwnSickLeaves(userEmail);
        model.addAttribute("own", true);
        model.addAttribute("sickLeaves", sickLeaves);
        return "sick-leaves-list";
        
    }
}
