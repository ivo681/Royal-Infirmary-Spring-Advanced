package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, String> {

    @Query("SELECT s FROM SickLeave s WHERE s.appointment.id = :appointmentId" +
            " AND s.md.email = :mdEmail")
    Optional<SickLeave> getSickLeaveByAppointmentIdAndMdEmail(String appointmentId, String mdEmail);

    @Query("SELECT s FROM SickLeave s WHERE s.appointment.id = :appointmentId")
    Optional<SickLeave> getSickLeaveByAppointmentId(String appointmentId);


    Optional<SickLeave> findByNumber(Long number);

    List<SickLeave> getAllByMdEmail(String mdEmail);

    List<SickLeave> getAllByPatientEmail(String patientEmail);
}
