package com.example.webmoduleproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundError extends BaseAppException{
    public NotFoundError(String message) {
        super(message);
    }
}
