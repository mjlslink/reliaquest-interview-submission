package com.reliaquest.api.service;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.models.EmployeeResponse;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class EmployeeService {

    private RestClient restClient;
    private final ModelMapper modelMapper;

    @Value("${dataserver.url}")
    @NotNull
    private String dataSourceUrl;

    @Autowired
    public EmployeeService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(dataSourceUrl)
                .build();
        log.info("Initialized EmployeeService with data source URL: {}", dataSourceUrl);
    }

    @Retryable(
            value = {HttpClientErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public EmployeeResponse getAllEmployees() {
        log.info("Fetching all employees from {}", dataSourceUrl);
        return restClient.get().retrieve().body(EmployeeResponse.class);
    }

    public List<Employee> getEmployeesByName(String searchString) {
        log.info("Searching for employees with name containing: {}", searchString);
        return getAllEmployees().getData().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        log.info("Fetching employee with ID: {}", id);
        return getAllEmployees().getData().stream()
                .filter(employee -> Objects.nonNull(employee.getId())
                        && employee.getId().toString().equals(id))
                .findFirst()
                .get(); // TODO: handle case where employee is not found
        //        EmployeeResponse response = restClient.get().uri("/{id}", id).retrieve().body(EmployeeResponse.class);
        //        return response.getData().get(0);
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

    public String deleteEmployeeById(String id) {
        return restClient.delete().uri("/deleteEmployee/{id}", id).retrieve().body(String.class);
    }
}
// TODO: use SSL to secure the API endpoints and implement the other methods as per the interface requirements.
