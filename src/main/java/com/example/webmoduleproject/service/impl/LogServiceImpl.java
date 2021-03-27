package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.LogEntity;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.repository.LogRepository;
import com.example.webmoduleproject.service.LogService;
import com.example.webmoduleproject.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {
    private final UserService userService;
    private final LogRepository logRepository;

    public LogServiceImpl(UserService userService, LogRepository logRepository) {
        this.userService = userService;
        this.logRepository = logRepository;
    }

    @Override
    public void createLog(String userEmail, String mdId, String action) {
        User patient = this.userService.findByEmail(userEmail);
        User doctor = this.userService.findById(mdId);
        LogEntity logEntity = new LogEntity();
        logEntity.setDoctor(doctor);
        logEntity.setPatient(patient);
        logEntity.setAction(action);
        logEntity.setDateTime(LocalDateTime.now());
        this.logRepository.save(logEntity);
    }

    @Override
    public void clearLogsFromDatabase() {
        this.logRepository.deleteAll();
    }
}
