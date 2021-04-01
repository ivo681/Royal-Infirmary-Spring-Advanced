package com.example.webmoduleproject.service.impl;

import com.example.webmoduleproject.model.entities.LogEntity;
import com.example.webmoduleproject.model.entities.User;
import com.example.webmoduleproject.model.service.LogServiceModel;
import com.example.webmoduleproject.model.view.LogViewModel;
import com.example.webmoduleproject.repository.LogRepository;
import com.example.webmoduleproject.service.AppointmentService;
import com.example.webmoduleproject.service.LogService;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {
    private final UserService userService;
    private final LogRepository logRepository;
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    public LogServiceImpl(UserService userService, LogRepository logRepository, AppointmentService appointmentService, ModelMapper modelMapper) {
        this.userService = userService;
        this.logRepository = logRepository;
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createLog(String userEmail, String action) {
        User user = this.userService.findByEmail(userEmail);
        LogEntity logEntity = new LogEntity();
        logEntity.setUser(user);
        logEntity.setAction(action);
        logEntity.setDateTime(LocalDateTime.now());
        this.logRepository.save(logEntity);
    }

    @Override
    public void clearLogsFromDatabase() {
        this.logRepository.deleteAll();
    }

    @Override
    public List<LogViewModel> getLogList() {
        List<LogServiceModel> logServiceModels = logRepository.getAllLogs().stream().map(log ->
                this.modelMapper.map(log, LogServiceModel.class))
                .collect(Collectors.toList());
        return logServiceModels.stream().map(logServiceModel ->
                this.modelMapper.map(logServiceModel, LogViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public long countByAction(String action) {
        return this.logRepository.getCountByAction(action);
    }
}
