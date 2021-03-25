package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.Prescription;
import com.example.webmoduleproject.model.view.PrescriptionListAllViewModel;
import com.example.webmoduleproject.model.view.PrescriptionViewModel;
import com.example.webmoduleproject.repository.PrescriptionRepository;
import com.example.webmoduleproject.service.AmbulatoryListService;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.PrescriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentService appointmentService;
    private final AmbulatoryListService ambulatoryListService;
    private final ModelMapper modelMapper;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, AppointmentService appointmentService, AmbulatoryListService ambulatoryListService, ModelMapper modelMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentService = appointmentService;
        this.ambulatoryListService = ambulatoryListService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean existingPrescriptionByAppointmentId(String appointmentId) {
        return this.prescriptionRepository.findByAppointmentId(appointmentId).isPresent();
    }

    @Override
    public void createNewPrescription(String appointmentId, String medicines) {
        Prescription prescription = new Prescription();
        prescription.setDate(LocalDate.now());
        prescription.setMd(this.appointmentService.getMdByAppointmentId(appointmentId));
        prescription.setPatient(this.appointmentService.getPatientByAppointmentId(appointmentId));
        prescription.setNumber(generateDocumentNumber());
        prescription.setPatientHomeAddress(prescription.getPatient().getAddress());
        prescription.setMdTelephoneNumber(prescription.getMd().getTelephone());
        prescription.setMedicines(medicines.trim());
        prescription.setAppointment(this.appointmentService.getAppointmentById(appointmentId));
        this.prescriptionRepository.save(prescription);
    }

    @Override
    public PrescriptionViewModel getPrescriptionByAppointmentId(String appointmentId) {
        return this.modelMapper.map(this.prescriptionRepository.
                findByAppointmentId(appointmentId).get(), PrescriptionViewModel.class);
    }

    @Override
    public List<PrescriptionListAllViewModel> getAllPrescriptionsByMdEmail(String userEmail) {
        return this.prescriptionRepository.findAllByMd_EmailOrderByDateDesc(userEmail).stream().map(
                prescription -> {
                    PrescriptionListAllViewModel allViewModel = this.modelMapper.map(prescription, PrescriptionListAllViewModel.class);
                    allViewModel.setAppointmentId(prescription.getAppointment().getId());
                    allViewModel.setFirstName(prescription.getPatient().getFirstName());
                    allViewModel.setLastName(prescription.getPatient().getLastName());
                    allViewModel.setIdNumber(prescription.getPatient().getIdNumber());
                    allViewModel.setDiagnosis(this.ambulatoryListService.getDiagnosisFromListByAppointmentId(allViewModel.getAppointmentId()));
                    return allViewModel;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionListAllViewModel> getAllPrescriptionsByPatientEmail(String userEmail) {
        return this.prescriptionRepository.findAllByPatient_EmailOrderByDateDesc(userEmail).stream().map(
                prescription -> {
                    PrescriptionListAllViewModel allViewModel = this.modelMapper.map(prescription, PrescriptionListAllViewModel.class);
                    allViewModel.setAppointmentId(prescription.getAppointment().getId());
                    allViewModel.setFirstName(prescription.getMd().getFirstName());
                    allViewModel.setLastName(prescription.getMd().getLastName());
                    allViewModel.setJob(prescription.getMd().getJob());
                    allViewModel.setHospitalId(prescription.getMd().getHospitalId());
                    allViewModel.setDiagnosis(this.ambulatoryListService.getDiagnosisFromListByAppointmentId(allViewModel.getAppointmentId()));
                    return allViewModel;
                }
        ).collect(Collectors.toList());
    }

    private Long generateDocumentNumber(){
        Random random = new Random();
        long documentNumber = (long) (100000000 + random.nextInt(900000000));
        while (this.prescriptionRepository.findByNumber(documentNumber).isPresent()){
            documentNumber = (long) (100000000 + random.nextInt(900000000));
        }
        return documentNumber;
    }
}
