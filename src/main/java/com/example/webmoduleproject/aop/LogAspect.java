package com.example.webmoduleproject.aop;

import com.example.webmoduleproject.model.binding.AppointmentBindingModel;
import com.example.webmoduleproject.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {
    private final LogService logService;

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("execution(* com.example.webmoduleproject.web." +
            "AppointmentsController.appointmentCreate(..))")
    public void appointmentCreateTrack(){

    }

    @After("appointmentCreateTrack()")
    public void afterAdvice(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        AppointmentBindingModel model = (AppointmentBindingModel) args[0];
        String userEmail = model.getUserEmail();
        String mdId = (String) args[2];
        String action = joinPoint.getSignature().getName();
        logService.createLog(userEmail, mdId, action);
        System.out.println(args);
    }
}
