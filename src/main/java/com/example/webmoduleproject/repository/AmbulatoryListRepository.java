package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.AmbulatoryList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulatoryListRepository extends JpaRepository<AmbulatoryList, String> {

    Optional<AmbulatoryList> findByNumber(Long number);

    Optional<AmbulatoryList> findByAppointmentId(String appointmentId);

    List<AmbulatoryList> findAllByMd_EmailOrderByDateDesc(String mdEmail);

    List<AmbulatoryList> findAllByPatient_EmailOrderByDateDesc(String patientEmail);

    @Query("SELECT a.diagnosis FROM AmbulatoryList a WHERE a.appointment.id = :appointmentId")
    Optional<String> findDiagnosisByAppointmentId(String appointmentId);
}
