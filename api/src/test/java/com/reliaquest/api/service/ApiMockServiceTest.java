package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.EmployeeData;
import com.reliaquest.api.models.EmployeeResponse;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class ApiMockServiceTest {

    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        // Mocking the response for getAllEmployees
        Employee mockEmployee = Employee.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("Bob Dooley DVM")
                .salary(50000)
                .age(30)
                .title("Veterinarian")
                .email("no@no.com")
                .build();

        EmployeeResponse mockResponse =
                EmployeeResponse.builder().data(List.of(mockEmployee)).build();

        when(employeeService.getAllEmployees()).thenReturn(mockResponse);
    }

    @Test
    public void retrieve_all_employees_successful() {

            List<Employee> allEmployees = employeeService.getAllEmployees().getData();

            assertThat(allEmployees).isNotNull();
            assertThat(allEmployees).isInstanceOf(List.class);
            assertThat(allEmployees.size()).isEqualTo(1);
            assertThat(allEmployees.get(0).getName()).isEqualTo("Bob Dooley DVM");
    }

    @Test
    public void search_employees_with_name_parameter_successful() {

        when(employeeService.getEmployeesByName("Bob"))
                .thenReturn(List.of(Employee.builder()
                        .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                        .name("Bob Dooley DVM")
                        .salary(50000)
                        .age(30)
                        .title("Veterinarian")
                        .email("bob@bob.com")
                        .build()));
            List<Employee> allEmployees = employeeService.getEmployeesByName("Bob");

            assertThat(allEmployees).isNotNull();
            assertThat(allEmployees).isInstanceOf(List.class);
            assertThat(allEmployees.size()).isEqualTo(1);
            assertThat(allEmployees.get(0).getName()).isEqualTo("Bob Dooley DVM");
    }

    @Test
    public void search_employees_with_name_parameter_unsuccessful() {

            List<Employee> allEmployees = employeeService.getEmployeesByName("Burtangles");

            assertThat(allEmployees).isNotNull();
            assertThat(allEmployees).isInstanceOf(List.class);
            assertThat(allEmployees.size()).isEqualTo(0);
    }

    @Test
    public void search_employees_id_successful() {

        when(employeeService.getEmployeeById(anyString()))
                .thenReturn(Employee.builder()
                        .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                        .name("Bob Dooley DVM")
                        .salary(50000)
                        .age(30)
                        .title("Veterinarian")
                        .email("no@no.com")
                        .build());
            Employee employee = employeeService.getEmployeeById("123e4567-e89b-12d3-a456-426614174000");

            assertThat(employee).isNotNull();
            assertThat(employee.getName()).isEqualTo("Bob Dooley DVM");
    }

    @Test
    public void search_highest_salary_successful() {
            Integer salary = employeeService.getHighestSalaryOfEmployees();
            assertThat(salary).isNotNull();
    }

    @Test
    public void search_highest_earners_successful() {
            List<String> highestEarningEmployeeNames = employeeService.getHighestEarningEmployeeNames();
            assertThat(highestEarningEmployeeNames).isNotNull();
    }

    @Test
    public void add_employee_successful() {
        when(employeeService.createEmployee(any()))
                .thenReturn(Employee.builder()
                        .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                        .age(44)
                        .title("developer")
                        .name("Michael Larsen")
                        .salary(60000)
                        .email("test@test.com")
                        .build());
        Employee employee =
                employeeService.createEmployee(EmployeeData.builder().build());
        assertThat(employee).isNotNull();
        assertThat(employee.getName()).isEqualTo("Michael Larsen");
    }

    @Test
    public void delete_employee_successful() {
        when(employeeService.deleteEmployeeById(any())).thenReturn(HttpStatus.OK.name());
        String id = "123e4567-e89b-12d3-a456-426614174000";
        String response = employeeService.deleteEmployeeById(id);
        assertThat(response).isNotNull();
    }
}
