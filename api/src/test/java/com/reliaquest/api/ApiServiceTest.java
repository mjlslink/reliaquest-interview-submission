package com.reliaquest.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.reliaquest.api.models.Employee;
import com.reliaquest.api.service.EmployeeService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void retrieve_all_employees_successful() {

        List<Employee> allEmployees = employeeService.getAllEmployees().getData();

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
        assertThat(allEmployees.size()).isEqualTo(50);
        assertThat(allEmployees.get(0).getName()).isEqualTo("Bob Dooley DVM");
    }

    @Test
    public void search_employees_with_name_parameter_successful() {

        List<Employee> allEmployees = employeeService.getEmployeesByName("Bob");

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
        assertThat(allEmployees.size()).isEqualTo(1);
        assertThat(allEmployees.get(0).getName()).isEqualTo("Bob Dooley DVM");
    }

    @Test
    public void search_employees_with_name_parameter_unsuccessful() {

        List<Employee> allEmployees = employeeService.getEmployeesByName("Michael Larsen");

        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees).isInstanceOf(List.class);
        assertThat(allEmployees.size()).isEqualTo(0);
    }

    @Test
    public void search_employees_id_successful() {

        Employee employee = employeeService.getEmployeeById("93d210ee-4a12-4f07-a161-c41eb6b1dd09");

        assertThat(employee).isNotNull();
        assertThat(employee.getName()).isEqualTo("Julianna Kihn MD");
    }

    @Test
    public void search_highest_salary_successful() {

        Integer salary = employeeService.getHighestSalaryOfEmployees();
        assertThat(salary).isNotNull();
        //        assertThat(salary).isEqualTo(495639); //TODO: the salaries change everytime the mock server is
        // restarted
    }

    @Test
    public void search_highest_earners_successful() {

        List<String> highestEarningEmployeeNames = employeeService.getHighestEarningEmployeeNames();
        assertThat(highestEarningEmployeeNames).isNotNull();
    }
}
