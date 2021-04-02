package com.example.webmoduleproject.web;

import com.example.webmoduleproject.exceptions.DocumentExtractDetailError;
import com.example.webmoduleproject.exceptions.NotFoundError;
import com.example.webmoduleproject.model.binding.SickLeaveBindingModel;
import com.example.webmoduleproject.model.service.documents.SickLeaveBindingServiceModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientSickLeaveDetails;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveViewModel;
import com.example.webmoduleproject.model.view.sickLeaves.SickLeaveListAllViewModel;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.SickLeaveService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public SickLeaveController(UserService userService, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, SickLeaveService sickLeaveService, ModelMapper modelMapper) {
        this.userService = userService;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.sickLeaveService = sickLeaveService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/new/{id}")
    public String sickLeaveBuildByAppointmentId(@PathVariable("id") String appointmentId,
                                                      Principal principal, Model model) throws NotFoundError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistByIdAndMdEmail(appointmentId, userEmail)
                && this.ambulatoryListService.existingListForAppointment(appointmentId)) {
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
        throw new NotFoundError("Appointment not found with this id and md email");
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @PostMapping("/create-{id}")
    public String sickLeaveCreate(@Valid @ModelAttribute("sickLeaveBindingModel") SickLeaveBindingModel
                                                     sickLeaveBindingModel,
                                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                             @PathVariable("id") String appointmentId,
                                             Principal principal) throws NotFoundError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistByIdAndMdEmail(appointmentId,userEmail )
                && this.ambulatoryListService.existingListForAppointment(appointmentId)) {
            if (!this.sickLeaveService.existingSickLeaveByAppointmentIdMdCheck(appointmentId, userEmail)) {
                if (bindingResult.hasErrors() || sickLeaveBindingModel.getFromDate() == null ||
                !sickLeaveBindingModel.getFromDate().equals(LocalDate.now())) {
                    if (!sickLeaveBindingModel.getFromDate().equals(LocalDate.now())) {
                        bindingResult.rejectValue("fromDate", "error.sickLeaveBindingModel", "The start date must be today");
                    }
                    redirectAttributes.addFlashAttribute("sickLeaveBindingModel", sickLeaveBindingModel);
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.sickLeaveBindingModel",
                            bindingResult);
                    return "redirect:/sick-leaves/new/" + appointmentId;
                    
                }
                this.sickLeaveService.createNewSickLeave(appointmentId, this.modelMapper.map(sickLeaveBindingModel, SickLeaveBindingServiceModel.class));
            }
            return "redirect:/sick-leaves/details/" + appointmentId;
        }
        throw new NotFoundError("Appointment not found with this id and md email");
    }

    @GetMapping("/details/{id}")
    public String ambulatoryListCreate(@PathVariable("id") String appointmentId,
                                             Model model, Principal principal) throws NotFoundError, DocumentExtractDetailError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesUserHaveAccessToDetails(appointmentId,userEmail ) &&
                this.sickLeaveService.existingSickLeaveByAppointmentId(appointmentId)) {
            try {
                SickLeaveViewModel sickLeaveView = this.sickLeaveService.getSickLeaveByAppointmentId(appointmentId);
                MdDocumentDetails mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(appointmentId);
                mdDetailsByAppointmentId.setTelephone(sickLeaveView.getMdTelephoneNumber());
                PatientSickLeaveDetails patientSickLeaveDetails = this.appointmentService.
                        getSickPatientViewModelByAppointmentId(appointmentId);
                patientSickLeaveDetails.setTelephone(sickLeaveView.getPatientTelephoneNumber());
                patientSickLeaveDetails.setJob(sickLeaveView.getPatientJob());
                patientSickLeaveDetails.setEmployer(sickLeaveView.getPatientEmployer());
                patientSickLeaveDetails.setAddress(sickLeaveView.getPatientHomeAddress());
                model.addAttribute("sickLeaveView", sickLeaveView);
                model.addAttribute("patientView", patientSickLeaveDetails);
                model.addAttribute("mdView", mdDetailsByAppointmentId);
                return "sick-leave-confirm";
            }
            catch (Exception e) {
                throw new DocumentExtractDetailError("Details for existing sick leave could not be extracted properly");
            }
        }
        throw new NotFoundError("Sick leave not found with this appointment and user email");
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
