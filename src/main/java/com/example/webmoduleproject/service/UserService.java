package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.service.CompleteProfileServiceModel;
import com.example.webmoduleproject.model.service.ContactDetailsServiceModel;
import com.example.webmoduleproject.model.service.EmploymentDetailsServiceModel;
import com.example.webmoduleproject.model.service.UserRegisterServiceModel;
import com.example.webmoduleproject.model.view.*;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void seedGps() throws IOException;

    void seedMds() throws IOException;

    void seedPatients() throws IOException;

    void registerAndLoginUser(UserRegisterServiceModel model);

    boolean hasTelephone(String email);

    boolean takenHospitalId(Long id);

    boolean emailExists(String email);

    List<UserRole> getUserRoleByEmail(String email);

    Long getHospitalId(String email);

    void completeProfile(CompleteProfileServiceModel profileServiceModel, String userEmail);

    boolean hasGp(String userEmail);

    List<GpViewModel> getAllGps(String userEmail);

    void setGpById(String userEmail, String id);

    void addMdJob(String userEmail, String job);

    boolean hasJob(String userEmail);

    boolean hasMdRole(String userEmail);

    boolean isGpInHospital(String userEmail);

    List<PatientListViewModel> getPatientListByGpEmail(String userEmail);

    UserDetailsViewModel getPatientDetails(String id);

    String getGpIdByUserEmail(String email);

    List<MdViewModel> getAllMdsExceptGps();

    String getMdFullNameById(String id);

    String getFullNameByUserEmail(String userEmail);

    List<GpViewModel> getAllGpsExcept(String mdId, String userEmail);

    boolean isPatientEmployedByEmail(String userEmail);

    User findByEmail(String userEmail);

    User findById(String id);

    List<PatientListViewModel> getPatientList();

    List<MdViewModel> getPersonnelList();

    UserDetailsViewModel getPatientDetailsByEmail(String userEmail);

    void changeContactDetails(String userEmail, ContactDetailsServiceModel serviceModel);

    void changeEmploymentDetails(String userEmail, EmploymentDetailsServiceModel map);

    boolean isUserEmployedInHospital(String userEmail);
}
