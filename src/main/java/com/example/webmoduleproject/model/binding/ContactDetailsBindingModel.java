package com.example.webmoduleproject.model.binding;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ContactDetailsBindingModel {
    private String newTelephone;
    private String newAddress;

    public ContactDetailsBindingModel() {
    }

    @NotBlank(message = "Telephone field cannot be empty")
    @Pattern(regexp =  "\\d{10,14}", message = "Please enter a valid 10 digit telephone number")
    public String getNewTelephone() {
        return newTelephone;
    }

    public void setNewTelephone(String newTelephone) {
        this.newTelephone = newTelephone;
    }

    @NotBlank(message = "Address field cannot be empty")
    @Length(min = 3, message = "Address must be at least 3 characters long")
    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }
}
