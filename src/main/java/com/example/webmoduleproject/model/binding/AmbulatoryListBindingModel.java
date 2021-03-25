package com.example.webmoduleproject.model.binding;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class AmbulatoryListBindingModel {
    private String diagnosis;
    private String medicines;

    public AmbulatoryListBindingModel() {
    }

    @NotBlank(message = "Diagnosis field cannot be left empty")
    @Length(min = 3, message = "Diagnosis must be a least 3 characters long")
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }
}
