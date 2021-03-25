package com.example.webmoduleproject.model.entities;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ambulatory_lists")
public class AmbulatoryList extends BaseDocumentEntity{
    private LocalDate date;
    private String diagnosis;
    private String medicines;

    public AmbulatoryList() {
    }

    @Column(name = "date", nullable = false)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Column(name = "medicines_prescribed", nullable = false, columnDefinition = "TEXT")
    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

}
