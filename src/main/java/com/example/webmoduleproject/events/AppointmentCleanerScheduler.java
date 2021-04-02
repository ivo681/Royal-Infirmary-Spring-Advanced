package com.example.webmoduleproject.events;

import com.example.webmoduleproject.service.AppointmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AppointmentCleanerScheduler {
    private final AppointmentService appointmentService;

    public AppointmentCleanerScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * *")
    public void updateNoShowAppointmentsStatus(){
        this.appointmentService.updateStatusofNoShowPatientsForAppointments();
    }

    @Transactional
    @Scheduled(cron = "0 53 23 * * *")
    public void deleteUnconfirmedAndUnattendedAppointmentsToday(){
        this.appointmentService.deleteUnconfirmedAndUnattendedAppointmentsFromDatabase();
    }

    @Transactional
    @Scheduled(cron = "0 56 23 * * *")
    public void updateCompletedAppointmentsStatus(){
        this.appointmentService.changeStatusOfCompletedAppointmentsForTheDay();
    }




}
