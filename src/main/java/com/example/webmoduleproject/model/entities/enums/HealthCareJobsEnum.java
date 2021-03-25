package com.example.webmoduleproject.model.entities.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum HealthCareJobsEnum {
    PHYSICIAN("Physician"),
    GP("General Practitioner"),
    ORTHOTIST("Orthotist"), NURSE("Nurse"), SONOGRAPHER("Sonographer"), RT("Respiratory Therapists"),
    RADIOLOGIST("Radiologist"), DIETITIAN("Dietitian"), SURGEON("Surgeon"),
    PSYCHIATRIST("Psychiatrist"), OPTICIAN("Optician"), ANESTHESIOLOGIST("Anesthesiologist"),
    CHIROPRACTOR("Chiropractor")
    ;
    private String name;

    HealthCareJobsEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
