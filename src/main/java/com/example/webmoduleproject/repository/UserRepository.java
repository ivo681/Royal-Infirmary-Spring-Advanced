package com.example.webmoduleproject.repository;

import com.example.webmoduleproject.model.entities.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByHospitalId(Long id);

    @Query("SELECT u FROM User u WHERE u.email <> :userEmail AND u.hospitalId is not null")
    List<User> getAllSpecialists(String userEmail);

    @Query("SELECT u FROM User u WHERE u.email <> :userEmail AND u.hospitalId is not null" +
            " AND u.job = 'General Practitioner'")
    List<User> getAllGpsExcept(String userEmail);

    @Cacheable(value = "allGps")
    @Query("SELECT u FROM User u WHERE u.hospitalId is not null" +
            " AND u.job = 'General Practitioner'")
    List<User> getAllGps();

    @Query("SELECT u FROM User u WHERE u.hospitalId is not null" +
            " AND u.job = 'General Practitioner' AND u.id <> :id" +
            " AND u.email <> :userEmail")
    List<User> getAllGpsExceptCurrent(String id, String userEmail);

    @Query("SELECT COUNT(u) FROM User u WHERE u.gp.id = :id")
    Long getNumberOfPatients(String id);

    @Query("SELECT u FROM User u WHERE " +
            "u.gp.email = :email")
    List<User> getAllPatientsByGpEmail(String email);

    @Query("SELECT u.gp.id FROM User u WHERE u.email = :email")
    Optional<String> getGpIdByUserEmail(String email);

    @Cacheable(value = "otherMds")
    @Query("SELECT u FROM User u WHERE u.hospitalId IS NOT NULL AND " +
            "u.job NOT IN (:positions)")
    List<User> getAllMdsNotGps(String[] positions);

    @Query("SELECT concat(u.firstName,' ', u.lastName) FROM User u WHERE u.id = :id")
    Optional<String> getFullNameById(String id);

    @Query("SELECT u FROM User u WHERE u.email = :userEmail AND " +
            "u.employer IS NOT NULL")
    Optional<User> getUserByEmailIfEmployed(String userEmail);

    @Query("SELECT u FROM User u WHERE u.hospitalId IS NULL")
    List<User> getAllPatients();

    @Query("SELECT u FROM User u WHERE u.hospitalId IS NOT NULL")
    List<User> getAllHospitalPersonnel();

    @Query("SELECT u FROM User u WHERE u.hospitalId IS NOT NULL AND u.email = :userEmail")
    Optional<User> getUserByEmailIfHospitalEmployee(String userEmail);

    @Query("SELECT u FROM User u WHERE u.id =:patientId AND " +
            "u.gp.email = :userEmail")
    Optional<User> isTheGpOfTheUserWithId(String userEmail, String patientId);

    @Query("SELECT u FROM User u WHERE u.telephone = :number")
    Optional<User> findByTelephoneNumber(String number);

    @Query("SELECT u FROM User u WHERE u.idNumber = :idNumber")
    Optional<User> findByIdNumber(String idNumber);
}
