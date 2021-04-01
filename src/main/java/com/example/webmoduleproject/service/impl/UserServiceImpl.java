package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.dtos.MdSeedDto;
import com.example.webmoduleproject.model.dtos.PatientSeedDto;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.entities.UserRole;
import com.example.webmoduleproject.model.entities.enums.RoleEnum;
import com.example.webmoduleproject.model.service.*;
import com.example.webmoduleproject.model.view.*;
import com.example.webmoduleproject.repository.UserRepository;
import com.example.webmoduleproject.repository.UserRoleRepository;
import com.example.webmoduleproject.service.UserDetailsService;
import com.example.webmoduleproject.service.UserService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final static String MDS_PATH = "src/main/resources/static/json/medics.json";
    private final static String PATIENTS_PATH = "src/main/resources/static/json/patients.json";
    private final static String GPS_PATH = "src/main/resources/static/json/gps.json";
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final UserDetailsService userDetailsService;
    private final Random random;

    public UserServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, Gson gson, UserDetailsService userDetailsService, Random random) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.userDetailsService = userDetailsService;
        this.random = random;
    }

    @Override
    public void seedGps() throws IOException {
        if (this.userRepository.count() == 0) {
            String content = String.join("", Files.readAllLines(Path.of(GPS_PATH)));

            MdSeedDto[] gpsSeedDtos = this.gson.fromJson(content, MdSeedDto[].class);
            User admin = null;
            UserRole mdRole = this.userRoleRepository.findByRole(RoleEnum.MD).get();
            UserRole patientRole = this.userRoleRepository.findByRole(RoleEnum.PATIENT).get();
            UserRole gpRole = this.userRoleRepository.findByRole(RoleEnum.GP).get();
            List<User> gps = new ArrayList<>();
            for (MdSeedDto gpsSeedDto : gpsSeedDtos) {
                User newUser = this.modelMapper.map(gpsSeedDto, User.class);
                newUser.setPassword(passwordEncoder.encode(gpsSeedDto.getPassword()));
                newUser.setTelephone("0" + gpsSeedDto.getTelephone());
                newUser.setHospitalId(generateHospitalId());
                newUser.setAddress("Sofia " + gpsSeedDto.getAddress());
                newUser.setEmployer("Royal Infirmary St. Sofia");
                newUser.setIdNumber(generateIdNumber(newUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("yy MM dd"))));
                ArrayList<UserRole> roles = new ArrayList<>();
                roles.add(mdRole);
                roles.add(patientRole);
                roles.add(gpRole);
                if (this.userRepository.count() == 0) {
                    roles.add(this.userRoleRepository.findByRole(RoleEnum.ADMIN).get());
                    newUser.setRoles(roles);
                    admin = this.userRepository.save(newUser);
                } else {
                    newUser.setRoles(roles);
                    newUser.setGp(admin);
                    gps.add(newUser);
                }

            }
            this.userRepository.saveAll(gps);
            admin.setGp(this.userRepository.findByEmail("katardiev@abv.bg").get());
            this.userRepository.save(admin);
        }
    }

    @Override
    public void seedMds() throws IOException {
        if (this.userRepository.count() == 8) {

            String content = String.join("", Files.readAllLines(Path.of(MDS_PATH)));
            List<User> allGps = this.userRepository.findAll();
            UserRole mdRole = this.userRoleRepository.findByRole(RoleEnum.MD).get();
            UserRole patientRole = this.userRoleRepository.findByRole(RoleEnum.PATIENT).get();
            MdSeedDto[] mdSeedDtos = this.gson.fromJson(content, MdSeedDto[].class);
            List<User> medics = new ArrayList<>();
            for (MdSeedDto mdSeedDto : mdSeedDtos) {
                User newUser = this.modelMapper.map(mdSeedDto, User.class);
                newUser.setPassword(passwordEncoder.encode(mdSeedDto.getPassword()));
                newUser.setTelephone("0" + mdSeedDto.getTelephone());
                newUser.setHospitalId(generateHospitalId());
                newUser.setAddress("Sofia " + mdSeedDto.getAddress());
                newUser.setEmployer("Royal Infirmary St. Sofia");
                ArrayList<UserRole> roles = new ArrayList<>();
                int index = getRandomGpIndex(allGps.size());
                newUser.setGp(allGps.get(index));
                if (!mdSeedDto.getJob().equalsIgnoreCase("nurse")) {
                    roles.add(mdRole);
                }
                roles.add(patientRole);
                newUser.setRoles(roles);
                newUser.setIdNumber(generateIdNumber(newUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("yy MM dd"))));
                medics.add(newUser);
            }
            this.userRepository.saveAll(medics);
        }
    }

    @Override
    public void seedPatients() throws IOException {
        if (this.userRepository.count() == 72) {
            List<User> newUsers = new ArrayList<>();
            String content = String.join("", Files.readAllLines(Path.of(PATIENTS_PATH)));
            PatientSeedDto[] patientSeedDtos = this.gson.fromJson(content, PatientSeedDto[].class);
            UserRole patientRole = this.userRoleRepository.findByRole(RoleEnum.PATIENT).get();

            List<User> allGps = this.userRepository.getAllGps();
            for (PatientSeedDto patientSeedDto : patientSeedDtos) {
                User newUser = this.modelMapper.map(patientSeedDto, User.class);
                newUser.setPassword(passwordEncoder.encode(patientSeedDto.getPassword()));
                newUser.setTelephone("0" + patientSeedDto.getTelephone());
                ArrayList<UserRole> roles = new ArrayList<>();
                roles.add(patientRole);
                newUser.setIdNumber(generateIdNumber(newUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("yy MM dd"))));
                int index = getRandomGpIndex(allGps.size());
                newUser.setGp(allGps.get(index));
                newUser.setAddress("Sofia " + patientSeedDto.getAddress());
                newUsers.add(newUser);
            }
            this.userRepository.saveAll(newUsers);
        }
    }

    //DONE
    private String generateIdNumber(String firstPart) {
        String secondPart = String.valueOf(this.random.nextInt(10000 - 999) + 999);
        return secondPart.length() < 4 ? String.format("%s0%s", firstPart.replace(" ", ""), secondPart) :
                String.format("%s%s", firstPart.replace(" ", ""), secondPart);
    }


    //DONE
    @Override
    public void registerAndLoginUser(UserRegisterServiceModel model) {
        User user = this.modelMapper.map(model, User.class);
        user.setPassword(passwordEncoder.encode(model.getPassword()));
        ArrayList<UserRole> roles = new ArrayList<>();
        if (model.getPosition().equalsIgnoreCase("doctor")) {
            roles.add(this.userRoleRepository.findByRole(RoleEnum.MD).get());
            user.setHospitalId(generateHospitalId());
        } else if (model.getPosition().equalsIgnoreCase("patient")) {
            roles.add(this.userRoleRepository.findByRole(RoleEnum.PATIENT).get());
        }
        user.setRoles(roles);
        this.userRepository.save(user);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //DONE-
    @Override
    public boolean hasTelephone(String email) {
        return this.userRepository.findByEmail(email).get().getTelephone() != null;
    }

    //DONE-
    @Override
    public boolean takenHospitalId(Long id) {
        return this.userRepository.findByHospitalId(id).isPresent();
    }

    //DONE-
    @Override
    public boolean emailExists(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<UserRole> getUserRoleByEmail(String email) {
        List<UserRole> userRoles = this.userRepository.findByEmail(email).get().getRoles();
        return userRoles;
    }

    @Override
    public Long getHospitalId(String email) {
        return this.userRepository.findByEmail(email).get().getHospitalId();
    }

    //DONE- ONLY UPDATE METHOD TO PUT
    @Override
    public void completeProfile(CompleteProfileServiceModel profileServiceModel, String userEmail) {
        User user = this.userRepository.findByEmail(userEmail).get();
        user.setIdNumber(profileServiceModel.getIdNumber());
        user.setTelephone(profileServiceModel.getTelephone());
        user.setAddress(profileServiceModel.getAddress());
        if (user.getHospitalId() == null) {
            if (profileServiceModel.getJob() != null && profileServiceModel.getEmployer() != null) {
                if (!profileServiceModel.getEmployer().trim().isBlank() && !profileServiceModel.getJob().trim().isBlank()) {
                    user.setJob(profileServiceModel.getJob());
                    user.setEmployer(profileServiceModel.getEmployer());
                } else {
                    user.setJob(null);
                    user.setEmployer(null);
                }
            } else {
                user.setJob(null);
                user.setEmployer(null);
            }
        } else {
            user.setEmployer("Royal Infirmary St. Sofia");
        }
        this.userRepository.save(user);
    }

    //DONE
    @Override
    public boolean hasGp(String userEmail) {
        return this.userRepository.findByEmail(userEmail).get().getGp() != null;
    }

    //DONE
    @Override
    public List<GpViewModel> getAllGps(String userEmail) {
        List<GpServiceModel> gpServiceModels = this.userRepository.getAllGpsExcept(userEmail).stream().map(doctor -> {
            GpServiceModel gpServiceModel = this.modelMapper.map(doctor, GpServiceModel.class);
            gpServiceModel.setAge((long) Period.between(doctor.getDateOfBirth(), LocalDate.now()).getYears());
            gpServiceModel.setNumberPatients(this.userRepository.getNumberOfPatients(doctor.getId()));
            return gpServiceModel;
        }).collect(Collectors.toList());

        return gpServiceModels.stream().map(gpServiceModel ->
                this.modelMapper.map(gpServiceModel, GpViewModel.class)).
                collect(Collectors.toList());
    }

    @Override
    public void setGpById(String userEmail, String id) {
        User user = this.userRepository.findByEmail(userEmail).get();
        user.setGp(this.userRepository.findById(id).get());
        this.userRepository.save(user);
    }

    //DONE
    @Override
    public void addMdJob(String userEmail, String job) {
        User user = this.userRepository.findByEmail(userEmail).get();
        if (user.getJob() == null) {
            user.setJob(job);
            this.userRepository.save(user);
        }
    }

    //DONE
    @Override
    public boolean hasJob(String userEmail) {
        return this.userRepository.findByEmail(userEmail).get().getJob() != null;
    }

    //DONE
    @Override
    public boolean hasMdRole(String userEmail) {
        List<UserRole> userRoleByEmail = getUserRoleByEmail(userEmail);
        Optional<UserRole> MdRole = userRoleByEmail.stream().filter(userRole ->
                userRole.getRole().equals(RoleEnum.MD)).findAny();
        return MdRole.isPresent();
    }

    //DONE
    @Override
    public boolean isGpInHospital(String userEmail) {
        return this.userRepository.getHospitalGpByEmail(userEmail).isPresent();
    }

    @Override
    public List<PatientListViewModel> getPatientListByGpEmail(String userEmail) {
        List<PatientListServiceModel> patientListServiceModels = this.userRepository.getAllPatientsByGpEmail(userEmail).
                stream().map(patient -> {
            PatientListServiceModel model = this.modelMapper.map(patient, PatientListServiceModel.class);
            int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
            model.setAge(age);
            return model;
        }).collect(Collectors.toList());

        return patientListServiceModels.stream().map(
                serviceModel -> this.modelMapper.map(serviceModel, PatientListViewModel.class)
        ).collect(Collectors.toList());
    }

    @Override
    public UserDetailsViewModel getPatientDetails(String id) {
        PatientDetailsServiceModel model = this.modelMapper.map(this.userRepository.findById(id).get(), PatientDetailsServiceModel.class);
        return this.modelMapper.map(model, UserDetailsViewModel.class);
    }

    //DONE
    @Override
    public String getGpIdByUserEmail(String email) {
        return this.userRepository.getGpIdByUserEmail(email).get();
    }

    @Override
    public List<MdViewModel> getAllMdsExceptGps() {
        List<MdServiceModel> mdServiceModels = this.userRepository.getAllMdsNotGps(new String[]{"" +
                "General Practitioner", "Nurse"}).stream()
                .map(md -> {
                    MdServiceModel map = this.modelMapper.map(md, MdServiceModel.class);
                    int age = Period.between(md.getDateOfBirth(), LocalDate.now()).getYears();
                    map.setAge(age);
                    return map;
                }).collect(Collectors.toList());

        return mdServiceModels.stream()
                .map(model -> this.modelMapper.map(model, MdViewModel.class))
                .collect(Collectors.toList());
    }

    //DONE
    @Override
    public String getMdFullNameById(String id) {
        return this.userRepository.getFullNameById(id).get();
    }

    //DONE
    @Override
    public String getFullNameByUserEmail(String userEmail) {
        return this.userRepository.getFullNameByUserEmail(userEmail).get();
    }

    @Override
    public List<GpViewModel> getAllGpsExcept(String mdId, String userEmail) {
        List<GpServiceModel> gpServiceModels = this.userRepository.getAllGpsExceptCurrent(mdId, userEmail).stream().map(doctor -> {
            GpServiceModel gpServiceModel = this.modelMapper.map(doctor, GpServiceModel.class);
            gpServiceModel.setAge((long) Period.between(doctor.getDateOfBirth(), LocalDate.now()).getYears());
            gpServiceModel.setNumberPatients(this.userRepository.getNumberOfPatients(doctor.getId()));
            return gpServiceModel;
        }).collect(Collectors.toList());

        return gpServiceModels.stream().map(gpServiceModel ->
                this.modelMapper.map(gpServiceModel, GpViewModel.class)).
                collect(Collectors.toList());
    }

    //DONE
    @Override
    public boolean isPatientEmployedByEmail(String userEmail) {
        return this.userRepository.getUserByEmailIfEmployed(userEmail).isPresent();
    }

    //DONE
    @Override
    public User findByEmail(String userEmail) {
        Optional<User> byEmail = this.userRepository.findByEmail(userEmail);
        return byEmail.orElse(null);
    }

    //DONE
    @Override
    public User findById(String id) {
        return this.userRepository.findById(id).get();
    }

    @Override
    public List<PatientListViewModel> getPatientList() {
        List<PatientListServiceModel> patientListServiceModels = this.userRepository.getAllPatients().
                stream().map(patient -> {
            PatientListServiceModel model = this.modelMapper.map(patient, PatientListServiceModel.class);
            int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
            model.setAge(age);
            return model;
        }).collect(Collectors.toList());

        return patientListServiceModels.stream().map(
                serviceModel -> this.modelMapper.map(serviceModel, PatientListViewModel.class)
        ).collect(Collectors.toList());
    }

    @Override
    public List<MdViewModel> getPersonnelList() {
        List<MdServiceModel> mdServiceModels = this.userRepository.getAllHospitalPersonnel().stream()
                .map(md -> {
                    MdServiceModel map = this.modelMapper.map(md, MdServiceModel.class);
                    int age = Period.between(md.getDateOfBirth(), LocalDate.now()).getYears();
                    map.setAge(age);
                    return map;
                }).collect(Collectors.toList());

        return mdServiceModels.stream()
                .map(model -> this.modelMapper.map(model, MdViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailsViewModel getPatientDetailsByEmail(String userEmail) {
        PatientDetailsServiceModel model = this.modelMapper.map(this.userRepository.findByEmail(userEmail).get(), PatientDetailsServiceModel.class);
        return this.modelMapper.map(model, UserDetailsViewModel.class);
    }

    @Override
    public void changeContactDetails(String userEmail, ContactDetailsServiceModel serviceModel) {
        User user = this.userRepository.findByEmail(userEmail).get();
        user.setTelephone(serviceModel.getNewTelephone());
        user.setAddress(serviceModel.getNewAddress());
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public void changeEmploymentDetails(String userEmail, EmploymentDetailsServiceModel serviceModel) {
        User user = this.userRepository.findByEmail(userEmail).get();
        if (serviceModel.getNewEmployer() != null && !serviceModel.getNewEmployer().trim().isBlank()
        && !serviceModel.getNewJob().trim().isBlank()){
            user.setEmployer(serviceModel.getNewEmployer().trim());
            user.setJob(serviceModel.getNewJob().trim());
        } else {
            user.setEmployer(null);
            user.setJob(null);
        }
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public boolean isUserEmployedInHospital(String userEmail) {
        return this.userRepository.getUserByEmailIfHospitalEmployee(userEmail).isPresent();
    }

    private Long generateHospitalId() {
        long hospitalId = (long) (100000 + this.random.nextInt(900000));
        while (takenHospitalId(hospitalId)) {
            hospitalId = (long) (100000 + this.random.nextInt(900000));
        }
        return hospitalId;
    }

    private int getRandomGpIndex(int length) {
        return this.random.nextInt(length);
    }




}
