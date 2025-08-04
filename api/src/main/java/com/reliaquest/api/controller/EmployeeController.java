package com.reliaquest.api.controller;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, EmployeeData> {

    private final EmployeeService employeeService;

    @Autowired
    EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @Operation(summary = "Fetches all employees")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Successful"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.of(
                Optional.ofNullable(employeeService.getAllEmployees().getData()));
    }

    @Override
    @Operation(summary = "Fetches employees containing the specified name search string")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Found the employee"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getEmployeesByName(searchString)));
    }

    @Override
    @Operation(summary = "Fetches employees by UUID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Found the employee"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getEmployeeById(id)));
    }

    @Override
    @Operation(summary = "Fetches employees with the highest salary")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Found the employee"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getHighestSalaryOfEmployees()));
    }

    @Override
    @Operation(summary = "Fetches top 10 highest-paid employees ")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Found the employee"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getHighestEarningEmployeeNames()));
    }

    @Override
    @Operation(summary = "Creates an employees ")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Employee created successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeData employeeInput) {
        return ResponseEntity.of(Optional.ofNullable(employeeService.createEmployee(employeeInput)));
    }

    @Override
    @Operation(summary = "deletes an employee by UUID ")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials"),
                @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
                @ApiResponse(responseCode = "404", description = "Employee not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error"),
            })
    public ResponseEntity<String> deleteEmployeeById(@Valid @PathVariable String id) {
        return ResponseEntity.of(Optional.ofNullable(employeeService.deleteEmployeeById(id)));
    }
}
