package com.example.webmoduleproject.service;

public interface LogService {
    void createLog(String userEmail, String mdId, String action);

    void clearLogsFromDatabase();
}
