package com.example.Training_system.exception;

import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public String handleDuplicate(ResourceAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleGeneric(RuntimeException ex) {
        return ex.getMessage();
    }
}