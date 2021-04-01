package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.Appointment;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    @Query("SELECT a FROM Appointment a WHERE a.id= :id AND a.md.email = :userEmail")
    Optional<Appointment>findByIdAndMd_Email(String id, String userEmail);

    @Query("SELECT a FROM Appointment a WHERE a.id= :id AND a.patient.email = :userEmail")
    Optional<Appointment>findByIdAndPatient_Email(String id, String userEmail);

    @Query("SELECT a FROM Appointment  a WHERE a.id = :id AND a.status = :status")
    Optional<Appointment> findUnconfirmedAppointmentById(String id, StatusEnum status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date AND a.timeSpan = :timeSpan" +
            " AND concat(a.md.firstName, ' ', a.md.lastName) = :mdName " +
            "AND a.md.hospitalId IS NOT NULL AND a.status = :confirmed")
    int findAllAppointmentsByDateAndTime(LocalDate date, String timeSpan, String mdName, StatusEnum confirmed);

    @Query("SELECT a FROM Appointment a WHERE a.date = :date AND " +
            "a.status = :status AND a.md.id = :mdId AND a.patient.email = :userEmail")
    Optional<Appointment> findConfirmedAppointmentForUser(LocalDate date, String userEmail, String mdId, StatusEnum status);

    @Query("SELECT a FROM Appointment a WHERE a.date = :date AND " +
            "a.status = :status AND a.md.id = :mdId AND a.patient.email = :userEmail")
    List<Appointment> findUnconfirmedAppointmentForUser(LocalDate date, String userEmail, String mdId, StatusEnum status);


    @Query("SELECT a FROM Appointment a WHERE a.patient.email = :userEmail" +
            " AND a.date < :date AND a.status = :status")
    List<Appointment> getPastAppointmentsForPatient(String userEmail, LocalDate date, StatusEnum status);

    @Query("SELECT a FROM Appointment a WHERE a.patient.email = :userEmail" +
            " AND a.date >= :date AND a.status = :status")
    List<Appointment> getFutureAppointmentsForPatient(String userEmail, LocalDate date, StatusEnum status);

    @Query("SELECT a FROM Appointment a WHERE a.md.email = :userEmail" +
            " AND a.date < :date AND a.md.hospitalId IS NOT NULL AND a.status = :status")
    List<Appointment> getPastAppointmentsForDoctor(String userEmail, LocalDate date, StatusEnum status);

    @Query("SELECT a FROM Appointment a WHERE a.md.email = :userEmail" +
            " AND a.date = :date AND a.md.hospitalId IS NOT NULL AND a.status = :status")
    List<Appointment> getTodaysAppointmentsForDoctor(String userEmail, LocalDate date, StatusEnum status);

    @Query("SELECT a FROM Appointment a WHERE a.md.email = :userEmail" +
            " AND a.date > :date AND a.md.hospitalId IS NOT NULL AND a.status = :status")
    List<Appointment> getFutureAppointmentsForDoctor(String userEmail, LocalDate date, StatusEnum status);

    @Query("SELECT a.md FROM Appointment a WHERE a.id = :id")
    Optional<User> getMdByMdId(String id);

    @Query("SELECT a.patient FROM Appointment a WHERE a.id = :id")
    Optional<User> getPatientByPatientId(String id);

    @Query("SELECT a FROM Appointment a WHERE a.status = :unconfirmed OR a.status = :noShow")
    List<Appointment> getUnconfirmedAndUnattendedAppointmentsFromDataBase(StatusEnum unconfirmed, StatusEnum noShow);

    @Modifying
    @Query("UPDATE Appointment a SET a.status= :closed WHERE " +
            "a.status = :confirmed AND a.date = :today")
    void updateTodaysConfirmedAppointmentStatus(StatusEnum confirmed, StatusEnum closed, LocalDate today);

    @Modifying
    @Query("UPDATE Appointment a SET a.status= :noshow WHERE " +
            "a.status = :confirmed AND a.date = :today AND a.id NOT IN (SELECT list.appointment.id FROM AmbulatoryList list)")
    void updateStatusOfNoShows(StatusEnum confirmed, StatusEnum noshow, LocalDate today);

    @Query("SELECT a FROM Appointment a WHERE a.id = :id AND " +
            "a.md.email = :userEmail")
    Optional<Appointment> isUserTheMdInAppointment(String userEmail, String id);
}
