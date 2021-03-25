package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.AmbulatoryList;
import com.example.webmoduleproject.model.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, String> {

    Optional<Prescription> findByAppointmentId(String id);

    Optional<Prescription> findByNumber(Long number);

    List<Prescription> findAllByMd_EmailOrderByDateDesc(String mdEmail);

    List<Prescription> findAllByPatient_EmailOrderByDateDesc(String patientEmail);


}
