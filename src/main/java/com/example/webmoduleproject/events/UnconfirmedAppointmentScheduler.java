package com.example.webmoduleproject.events;

import com.example.webmoduleproject.service.AppointmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnconfirmedAppointmentScheduler {
    private final AppointmentService appointmentService;

    public UnconfirmedAppointmentScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Transactional
    @Scheduled(cron = "0 7 0 * * *")
    public void updateNoShowAppointmentsStatus(){
        this.appointmentService.updateStatusofNoShowPatientsForAppointments();
    }

    @Transactional
    @Scheduled(cron = "0 15 3 * * *")
    public void deleteUnconfirmedAndUnattendedAppointmentsToday(){
        this.appointmentService.deleteUnconfirmedAndUnattendedAppointmentsFromDatabase();
    }

    @Transactional
    @Scheduled(cron = "0 55 23 * * *")
    public void updateCompletedAppointmentsStatus(){
        this.appointmentService.changeStatusOfCompletedAppointmentsForTheDay();
    }




}
