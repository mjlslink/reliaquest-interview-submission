package com.reliaquest.api.controller;

import com.reliaquest.api.models.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    // TODO make this more specific to the EmployeeController, also add support for status
    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {

        log.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(EmployeeResponse.error(ex.getMessage()));
    }
}
