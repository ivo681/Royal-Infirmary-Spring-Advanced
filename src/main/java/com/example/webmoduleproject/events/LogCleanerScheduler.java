package com.example.webmoduleproject.events;

import com.example.webmoduleproject.service.LogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LogCleanerScheduler {
    private final LogService logService;

    public LogCleanerScheduler(LogService logService) {
        this.logService = logService;
    }

    @Transactional
    @Scheduled(cron = "0 59 23 * * *")
    public void updateNoShowAppointmentsStatus(){
        this.logService.clearLogsFromDatabase();
    }
}
