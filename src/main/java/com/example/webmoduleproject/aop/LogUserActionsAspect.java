package com.example.webmoduleproject.aop;

import com.example.webmoduleproject.model.binding.AppointmentBindingModel;
import com.example.webmoduleproject.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;

@Aspect
@Component
public class LogUserActionsAspect {
    private final LogService logService;

    public LogUserActionsAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("execution(* com.example.webmoduleproject.service.impl." +
            "AppointmentServiceImpl.appointmentCreate(..))")
    public void appointmentCreateTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.service.impl." +
            "AppointmentServiceImpl.confirmAppointmentById(..))")
    public void appointmentConfirmTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.web." +
            "UsersController.cancelFutureAppointment(..))")
    public void appointmentCancelTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.web." +
            "UsersController.changeGp(..))")
    public void changeGpTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.web." +
            "UsersController.changeUserContactDetails(..))")
    public void changeUserContactDetailsTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.web." +
            "UsersController.changeUserEmploymentDetails(..))")
    public void changeUserEmploymentDetailsTrack(){
    }

    @After("appointmentCreateTrack()")
    public void afterAppointmentCreateAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("appointmentConfirmTrack()")
    public void afterAppointmentConfirmAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("appointmentCancelTrack()")
    public void afterAppointmentCancelAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("changeGpTrack()")
    public void afterChangeGpAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("changeUserContactDetailsTrack()")
    public void afterChangeUserContactDetailsAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("changeUserEmploymentDetailsTrack()")
    public void afterChangeUserEmploymentDetailsAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }
}
