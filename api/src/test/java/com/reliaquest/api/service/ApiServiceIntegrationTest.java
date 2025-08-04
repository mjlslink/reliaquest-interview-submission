package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class ApiServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        EmployeeData employeeData = EmployeeData.builder()
                .id(UUID.fromString("46b22daa-7464-4bf9-865b-9db0153d1d9c")) //the server replaces this with a new UUID
                .name("Test Employee")
                .salary(50000)
                .age(30)
                .title("Tester")
                .email("none@none.com")
                .build();
        employee = employeeService.createEmployee(employeeData);
    }

    @Test
    public void retrieve_all_employees_successful() {

        List<Employee> allEmployees = employeeService.getAllEmployees().getData();

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
    }

    @Test
    public void search_employees_with_name_parameter_successful() {

        List<Employee> allEmployees = employeeService.getEmployeesByName("Codi");

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
    }

    @Test
    public void search_employees_with_name_parameter_unsuccessful() {

        List<Employee> allEmployees = employeeService.getEmployeesByName("Burtangles");

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
        assertThat(allEmployees.size()).isEqualTo(0);
    }

//    @Test
    public void search_employees_id_successful() {
        Employee employee = employeeService.getEmployeeById("a2fd7c5c-65cd-40c2-b5ec-425a5c0ecd5d");

        assertThat(employee).isNotNull();
        assertThat(employee.getName()).isEqualTo("Test Employee");
    }

    @Test
    public void search_highest_salary_successful() {

        Integer salary = employeeService.getHighestSalaryOfEmployees();
        assertThat(salary).isNotNull();
    }

    @Test
    public void search_highest_earners_successful() {
        try {
            List<String> highestEarningEmployeeNames = employeeService.getHighestEarningEmployeeNames();
            assertThat(highestEarningEmployeeNames).isNotNull();
        } catch (HttpClientErrorException.TooManyRequests ex) {
            List<Employee> fallbackEmployees =
                    employeeService.fallbackDefaultEmptyList(ex).getData();
            assertThat(fallbackEmployees).isEmpty();
        }
    }

    @Test
    public void add_employee_successful() {
        EmployeeData data = EmployeeData.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .age(44)
                .title("developer")
                .name("Michael Larsen")
                .salary(60000)
                .email("test@test.com")
                .build();
        employee = employeeService.createEmployee(data);
        assertThat(employee).isNotNull();
    }

    @Test
    public void delete_employee_successful() {
        String id = "46b22daa-7464-4bf9-865b-9db0153d1d9c";
        String response = employeeService.deleteEmployeeById(id);
        assertThat(response).isNotNull();
    }
}
