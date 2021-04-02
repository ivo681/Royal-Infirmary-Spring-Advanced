package com.example.webmoduleproject.model.entities;

import com.example.webmoduleproject.model.entities.enums.HealthCareJobsEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity{
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String telephone;
    private String email;
    private String password;
    private List<UserRole> roles;
    private String job;
    private Long hospitalId;
    private String idNumber;
    private User gp;
    private String employer;
    private String address;

    public User() {
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "date_of_birth", nullable = false)
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "telephone", unique = true)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    @Column(name = "id_number", unique = true)
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @ManyToOne
    public User getGp() {
        return gp;
    }

    public void setGp(User gp) {
        this.gp = gp;
    }

    @Column(name = "employer")
    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @Column(name = "job")
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Column(name = "hospital_id")
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
