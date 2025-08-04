package com.reliaquest.api.service;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.models.EmployeeResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class EmployeeService {

    private RestClient restClient;
    private final ModelMapper modelMapper;

    @Value("${dataserver.url}")
    @NotNull private String dataSourceUrl;

    @Autowired
    public EmployeeService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void init() {
        this.restClient = restClient(dataSourceUrl);
        log.info("Initialized EmployeeService with data source URL: {}", dataSourceUrl);
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @Cacheable("employees")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyList")
    public EmployeeResponse getAllEmployees() {
        log.info("Fetching all employees from {}", dataSourceUrl);
        return restClient.get().retrieve().body(EmployeeResponse.class);
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyList")
    public List<Employee> getEmployeesByName(String searchString) {
        log.info("Searching for employees with name containing: {}", searchString);
        return getAllEmployees().getData().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyEmployee")
    public Employee getEmployeeById(@NotNull String id) {
        log.info("Fetching employee with ID: {}", id);
        return getAllEmployees().getData().stream()
                .filter(employee -> Objects.nonNull(employee.getId().toString())
                        && employee.getId().toString().equals(id))
                .findFirst()
                .orElse(null);
//        return restClient.get()
//                .uri(dataSourceUrl + "/" + id)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .body(Employee.class);
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyList")
    public Integer getHighestSalaryOfEmployees() {
        log.info("Fetching highest salary from all employees");
        return getAllEmployees().getData().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(null);
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyList")
    public List<String> getHighestEarningEmployeeNames() {
        log.info("Fetching names of employees with the highest salary");
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
    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDefaultEmptyEmployee")
    public Employee createEmployee(@NonNull EmployeeData employeeInput) {
        log.info("Creating employee with input: {}", employeeInput);
        return restClient
                .post()
                .uri("/createEmployee")
                .accept(MediaType.APPLICATION_JSON)
                .body(employeeInput)
                .retrieve()
                .body(Employee.class);
    }

    @Retryable(
            value = {org.springframework.web.client.HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @CircuitBreaker(name = "employeeService", fallbackMethod = "fallbackDeleteEmployeeById")
    public String deleteEmployeeById(String id) {
        Employee employeeById = getEmployeeById(id);

        log.info("Deleting employee with ID: {}", id);
        // server requires a name parameter, and accepts only RequestBody
        Map<String, String> params = Map.of("name", employeeById.getName());
        return restClient
                .method(HttpMethod.DELETE)
                .uri(dataSourceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .body(String.class);
    }

    @Bean
    public RestClient restClient(@Value("${dataserver.url}") String baseUrl) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(10);

        CloseableHttpClient httpClient =
                HttpClients.custom().setConnectionManager(cm).build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        log.info("Creating RestClient pool wih {} connections on base URL: {}", 50, baseUrl);

        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    EmployeeResponse fallbackDefaultEmptyList(Throwable ex) {
        log.error("Rate limit exceeded. Returning fallback response.", ex);
        return new EmployeeResponse(Collections.emptyList(), "error", "Rate limit exceeded");
    }

    Employee fallbackDefaultEmptyEmployee(Throwable ex) {
        log.error("Rate limit exceeded. Returning fallback response.", ex);
        return Employee.builder().build();
    }


    String fallbackDeleteEmployeeById(String id, Throwable ex) {
        log.error("Rate limit exceeded for delete operation.", ex);
        //in this case, we return an empty string as the fallback response
        return "";
    }

}
// TODO: use SSL to secure the API endpoints and implement the other methods as per the interface requirements.
