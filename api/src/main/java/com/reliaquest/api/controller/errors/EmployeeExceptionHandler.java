package com.reliaquest.api.controller.errors;

import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class EmployeeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<String> handleNoSuchElementException(HttpServletRequest req, NoSuchElementException ex) {
        return ResponseEntity.status(404).body("Resource not found: " + ex.getMessage());
    }

    protected ResponseEntity<Object> createErrorResponseEntity(ErrorResponse response) {
        return new ResponseEntity<Object>(response, response.getStatus());
    }
}
