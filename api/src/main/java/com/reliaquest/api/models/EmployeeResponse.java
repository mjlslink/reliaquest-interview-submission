package com.reliaquest.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmployeeResponse {
    private List<Employee> data;
    private String status;
    private String error;

    public static Object error(String message) {
        return new EmployeeResponse(null, "error", message);
    }
}
