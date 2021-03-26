package com.example.webmoduleproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class PermissionError extends BaseAppException{
    public PermissionError(String message) {
        super(message);
    }
}
