package com.reliaquest.api.service;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.models.EmployeeResponse;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class EmployeeService {

    private final RestClient restClient;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeService(ModelMapper modelMapper) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8112/api/v1/employee")
                .build();
        this.modelMapper = modelMapper;
    }

    public EmployeeResponse getAllEmployees() {
        return restClient.get().retrieve().body(EmployeeResponse.class);
    }

    public List<Employee> getEmployeesByName(String searchString) {
        return getAllEmployees().getData().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        return restClient.get().uri("/{id}", id).retrieve().body(Employee.class);
    }

    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().getData().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(null); // TODO: review this, should it return 0 or null?
    }

    public List<String> getHighestEarningEmployeeNames() {
        List<Employee> employees = getAllEmployees().getData();

        Integer maxSalary =
                employees.stream().mapToInt(Employee::getSalary).max().orElseThrow();

        log.info("Max salary is {}", maxSalary);

        return employees.stream()
                .filter(employee -> employee.getSalary() < maxSalary)
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    // TODO: remove modelMapper if not necessary
    public Employee createEmployee(@NonNull EmployeeData employeeInput) {
        return restClient
                .post()
                .uri("/createEmployee")
                // .accept(MediaType.APPLICATION_JSON)
                .body(employeeInput)
                .retrieve()
                .body(Employee.class);
    }

    private Employee toEntity(EmployeeData input) {
        return modelMapper.map(input, Employee.class);
    }
}
// TODO: use SSL to secure the API endpoints and implement the other methods as per the interface requirements.
