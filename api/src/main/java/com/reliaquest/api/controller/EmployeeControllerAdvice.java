package com.reliaquest.api.controller;

import com.reliaquest.api.models.EmployeeResponse;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    // THis is a global exception handler for the EmployeeController
    // a 500 will be caught here and a response will be returned
    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {

        log.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(EmployeeResponse.error(ex.getMessage()));
    }

    // THe handler for 404 not found exceptions
    @ExceptionHandler({NoSuchElementException.class, ResponseStatusException.class})
    protected ResponseEntity<?> handleException(Exception ex) {
        // exception must be of a type that is thrown by the controller methods
        String message = ex instanceof ResponseStatusException
                        && ((ResponseStatusException) ex).getStatusCode() == HttpStatus.NOT_FOUND
                ? ex.getMessage()
                : "Resource not found";

        log.error("404 not found {}", message);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(EmployeeResponse.error(message));
    }
}
