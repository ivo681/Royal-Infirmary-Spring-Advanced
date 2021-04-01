package com.example.webmoduleproject.aop;

import com.example.webmoduleproject.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogDocumentsAspect {
    private final LogService logService;

    public LogDocumentsAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("execution(* com.example.webmoduleproject.service.impl." +
            "AmbulatoryListServiceImpl.createNewList(..))")
    public void ambulatoryListCreateTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.service.impl." +
            "PrescriptionServiceImpl.createNewPrescription(..))")
    public void prescriptionCreateTrack(){
    }

    @Pointcut("execution(* com.example.webmoduleproject.service.impl." +
            "SickLeaveServiceImpl.createNewSickLeave(..))")
    public void sickLeaveCreateTrack(){
    }

    @After("ambulatoryListCreateTrack()")
    public void afterAmbulatoryListCreateAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("prescriptionCreateTrack()")
    public void afterPrescriptionCreateAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }

    @After("sickLeaveCreateTrack()")
    public void afterSickLeaveCreateAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth != null ? auth.getName() : "";
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, action);
    }


}
