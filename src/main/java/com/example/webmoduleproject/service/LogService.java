package com.example.webmoduleproject.service;

import com.example.webmoduleproject.model.view.LogViewModel;

import java.util.List;

public interface LogService {
    void createLog(String userEmail,String action);

    void clearLogsFromDatabase();

    List<LogViewModel> getLogList();

    long countByAction(String action);
}
