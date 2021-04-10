package com.example.webmoduleproject.web;

import com.example.webmoduleproject.exceptions.DocumentExtractDetailError;
import com.example.webmoduleproject.exceptions.NotFoundError;
import com.example.webmoduleproject.model.binding.AmbulatoryListBindingModel;
import com.example.webmoduleproject.model.service.documents.AmbulatoryListServiceModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListAllViewModel;
import com.example.webmoduleproject.model.view.ambulatoryLists.AmbulatoryListViewModel;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientAmbulatoryListDetails;
import com.example.webmoduleproject.service.*;
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
@RequestMapping("/ambulatory-lists")
public class AmbulatoryListController {
    private final UserService userService;
    private final AmbulatoryListService ambulatoryListService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final SickLeaveService sickLeaveService;
    private final ModelMapper modelMapper;

    public AmbulatoryListController(UserService userService, AmbulatoryListService ambulatoryListService, AppointmentService appointmentService, PrescriptionService prescriptionService, SickLeaveService sickLeaveService, ModelMapper modelMapper) {
        this.userService = userService;
        this.ambulatoryListService = ambulatoryListService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
        this.sickLeaveService = sickLeaveService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/new/{id}")
    public String ambulatoryListBuild(@PathVariable("id") String id,
                                      Principal principal, Model model) throws NotFoundError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistByIdAndMdEmail(id, userEmail)) {
            if (!this.ambulatoryListService.existingListForAppointment(id)) {
                if (!model.containsAttribute("ambulatoryListBindingModel")) {
                    model.addAttribute("ambulatoryListBindingModel", new AmbulatoryListBindingModel());
                }
                MdDocumentDetails mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(id);
                PatientAmbulatoryListDetails patientViewModelByAppointmentId = this.appointmentService.getPatientViewModelByAppointmentId(id);
                model.addAttribute("mdViewModel", mdDetailsByAppointmentId);
                model.addAttribute("patientViewModel", patientViewModelByAppointmentId);
                model.addAttribute("appId", id);
                model.addAttribute("today", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return "ambulatory";
            }
            return "redirect:/ambulatory-lists/details/" + id;
        }
        throw new NotFoundError("Appointment not found with this id and md email");
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @PostMapping("/create-{id}")
    public String ambulatoryListCreate(@Valid @ModelAttribute("ambulatoryListBindingModel") AmbulatoryListBindingModel
                                               ambulatoryListBindingModel,
                                       BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                       @PathVariable("id") String appointmentId, Principal principal) throws NotFoundError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesAppointmentExistByIdAndMdEmail(appointmentId, userEmail)) {
            if (!this.ambulatoryListService.existingListForAppointment(appointmentId)) {
                if (bindingResult.hasErrors()) {
                    redirectAttributes.addFlashAttribute("ambulatoryListBindingModel", ambulatoryListBindingModel);
                    redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ambulatoryListBindingModel",
                            bindingResult);
                    return "redirect:/ambulatory-lists/new/" + appointmentId;
                }
                this.ambulatoryListService.createNewList(appointmentId,
                        this.modelMapper.map(ambulatoryListBindingModel, AmbulatoryListServiceModel.class));
                if (!ambulatoryListBindingModel.getMedicines().trim().isBlank()) {
                    this.prescriptionService.createNewPrescription(appointmentId, ambulatoryListBindingModel.getMedicines());
                }
            }
            return "redirect:/ambulatory-lists/details/" + appointmentId;
        }
        throw new NotFoundError("Appointment not found with this id and md email");
    }

    @GetMapping("/details/{id}")
    public String getExistingAmbulatoryList(@PathVariable("id") String appointmentId, Model model, Principal principal)
            throws NotFoundError, DocumentExtractDetailError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesUserHaveAccessToDetails(appointmentId,userEmail ) &&
                this.ambulatoryListService.existingListForAppointment(appointmentId)) {
            try {
                AmbulatoryListViewModel ambulatoryListByAppointmentId = this.ambulatoryListService.getAmbulatoryListByAppointmentId(appointmentId);
                MdDocumentDetails mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(appointmentId);
                PatientAmbulatoryListDetails patientViewModelByAppointmentId = this.appointmentService.getPatientViewModelByAppointmentId(appointmentId);
                mdDetailsByAppointmentId.setTelephone(ambulatoryListByAppointmentId.getMdTelephoneNumber());
                patientViewModelByAppointmentId.setTelephone(ambulatoryListByAppointmentId.getPatientTelephoneNumber());
                patientViewModelByAppointmentId.setAddress(ambulatoryListByAppointmentId.getPatientHomeAddress());
                model.addAttribute("mdViewModel", mdDetailsByAppointmentId);
                model.addAttribute("patientViewModel", patientViewModelByAppointmentId);
                model.addAttribute("ambulatoryList", ambulatoryListByAppointmentId);
                if (this.appointmentService.isUserTheMdInAppointment(userEmail, appointmentId)) {
                    model.addAttribute("patientEmployed", this.userService.isPatientEmployedByEmail(patientViewModelByAppointmentId.getEmail()));
                    model.addAttribute("showButtons", LocalDate.now().equals(ambulatoryListByAppointmentId.getDate()));
                } else {
                    model.addAttribute("patientEmployed", false);
                    model.addAttribute("showButtons", false);
                }
                model.addAttribute("existingSickLeave", sickLeaveService.existingSickLeaveByAppointmentId(appointmentId));
                model.addAttribute("showPrescription", !ambulatoryListByAppointmentId.getMedicines().isBlank());
                return "ambulatory-confirm";
            } catch (Exception e) {
                throw new DocumentExtractDetailError("Details for existing ambulatory list could not be extracted properly");
            }
        }
        throw new NotFoundError("Ambulatory list not found with this appointment id");
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


