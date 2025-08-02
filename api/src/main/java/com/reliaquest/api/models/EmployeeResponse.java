package com.reliaquest.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // TODO: necesary?
@AllArgsConstructor
public class EmployeeResponse {
    private List<Employee> data; // needs to match JSON response
    private String status;
    private String error;

    // TODO: consider using a builder pattern for more complex responses
    public static Object error(String message) {
        return new EmployeeResponse(null, "error", message);
    }
}
