package com.example.webmoduleproject.web;

import com.example.webmoduleproject.exceptions.DocumentExtractDetailError;
import com.example.webmoduleproject.exceptions.NotFoundError;
import com.example.webmoduleproject.model.view.buildBlocks.MdDocumentDetails;
import com.example.webmoduleproject.model.view.buildBlocks.PatientPrescriptionDetails;
import com.example.webmoduleproject.model.view.prescriptions.PrescriptionListAllViewModel;
import com.example.webmoduleproject.model.view.prescriptions.PrescriptionViewModel;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.PrescriptionService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService, UserService userService, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/details/{id}")
    public String getExistingPrescription(@PathVariable("id") String appointmentId,
                                                Model model, Principal principal) throws NotFoundError, DocumentExtractDetailError {
        String userEmail = principal.getName();
        if (this.appointmentService.doesUserHaveAccessToDetails(appointmentId,userEmail ) &&
                this.prescriptionService.existingPrescriptionByAppointmentId(appointmentId)) {
            try {
                PrescriptionViewModel prescriptionViewModel = this.prescriptionService.getPrescriptionByAppointmentId(appointmentId);
                MdDocumentDetails mdDetailsByAppointmentId = this.appointmentService.getMdDetailsByAppointmentId(appointmentId);
                mdDetailsByAppointmentId.setTelephone(prescriptionViewModel.getMdTelephoneNumber());
                PatientPrescriptionDetails patientViewModelByAppointmentId = this.appointmentService.getPatientPrescriptionViewModelByAppointmentId(appointmentId);
                patientViewModelByAppointmentId.setAddress(prescriptionViewModel.getPatientHomeAddress());
                model.addAttribute("mdViewModel", mdDetailsByAppointmentId);
                model.addAttribute("patientViewModel", patientViewModelByAppointmentId);
                model.addAttribute("prescription", prescriptionViewModel);
                return "prescription";
            } catch (Exception e) {
                throw new DocumentExtractDetailError("Details for existing prescription could not be extracted properly");
            }
        }
        throw new NotFoundError("Prescription was not found with this appointment id and user email");

    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/all")
    public String getAllPrescriptionsByMd(Principal principal, Model model) {
        String userEmail = principal.getName();
        List<PrescriptionListAllViewModel> prescriptions = this.prescriptionService.getAllPrescriptionsByMdEmail(userEmail);
        model.addAttribute("own", false);
        model.addAttribute("prescriptions", prescriptions);
        return "prescriptions-list";
    }

    @GetMapping("/own")
    public String getOwnAmbulatoryListsByUserEmail(Principal principal, Model model) {
        String userEmail = principal.getName();
        List<PrescriptionListAllViewModel> prescriptions = this.prescriptionService.getAllPrescriptionsByPatientEmail(userEmail);
        model.addAttribute("own", true);
        model.addAttribute("prescriptions", prescriptions);
        return "prescriptions-list";
    }
}
