package com.reliaquest.api.controller;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.models.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;
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
    public ResponseEntity<List<Employee>> getAllEmployees() {

        log.info("Fetching all employees");
        EmployeeResponse allEmployees = employeeService.getAllEmployees();
        return ResponseEntity.of(
                Optional.ofNullable(employeeService.getAllEmployees().getData()));
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("Found search string: {}", searchString);
        return ResponseEntity.of(Optional.ofNullable(employeeService.getEmployeesByName(searchString)));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("Fetching employee with ID: {}", id);
        return ResponseEntity.of(Optional.ofNullable(employeeService.getEmployeeById(id)));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getHighestSalaryOfEmployees()));
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.of(Optional.ofNullable(employeeService.getHighestEarningEmployeeNames()));
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeData employeeInput) {
        return ResponseEntity.of(Optional.ofNullable(employeeService.createEmployee(employeeInput)));
    }

    @Override
    @DeleteMapping
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }
}
