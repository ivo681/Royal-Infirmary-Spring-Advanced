package com.example.webmoduleproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DocumentExtractDetailError extends BaseAppException{
    public DocumentExtractDetailError(String message) {
        super(message);
    }
}
