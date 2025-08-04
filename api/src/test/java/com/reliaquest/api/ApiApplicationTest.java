package com.reliaquest.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.models.Employee;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class ApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeController employeeController;

    @Test
    public void retrieve_all_employees_successful() throws Exception {

        when(employeeController.getAllEmployees())
                .thenReturn(ResponseEntity.ok(List.of(
                        new Employee(UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "none@null.org"))));

        this.mockMvc.perform(get("/api/v1/employee")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void retrieve_one_employees_successful() throws Exception {

        when(employeeController.getEmployeeById("ID"))
                .thenReturn(ResponseEntity.ok(
                        new Employee(UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "none@null.org")));

        this.mockMvc.perform(get("/api/v1/employee/ID")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void retrieve_one_employees_not_found() throws Exception {

        when(employeeController.getEmployeeById("ID4544545"))
                .thenReturn(ResponseEntity.notFound().build());

        this.mockMvc.perform(get("/api/v1/employee/ID4544545")).andDo(print()).andExpect(status().isNotFound());
    }


    @Test
    public void search_employees_successful() throws Exception {

        when(employeeController.getEmployeesByNameSearch("Michael Larsen"))
                .thenReturn(ResponseEntity.ok(List.of(
                        new Employee(UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "none@null.org"))));

        this.mockMvc
                .perform(get("/api/v1/employee/search/Michael Larsen"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void search_employees_not_found() throws Exception {

        when(employeeController.getEmployeesByNameSearch("Michaellllll"))
                .thenReturn(ResponseEntity.notFound().build());

        this.mockMvc
                .perform(get("/api/v1/employee/search/Michaellllll"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    public void create_employee_successful() throws Exception {

        Employee employee = new Employee(UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "none@null.org");

        when(employeeController.createEmployee(any())).thenReturn(ResponseEntity.ok(employee));

        String employeeJson =
                """
        {
            "name": "Michael Larsen",
            "salary": 60000,
            "age": 44,
            "title": "Developer",
            "email": "none@null.org"
        }
        """;

        this.mockMvc
                .perform(
                        post("/api/v1/employee").contentType("application/json").content(employeeJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void create_employee_email_optional_successful() throws Exception {

        // This test is to ensure that the email field can be empty, as per the requirement.
        Employee employee = new Employee(UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "");

        when(employeeController.createEmployee(any())).thenReturn(ResponseEntity.ok(employee));

        String employeeJson =
                """
        {
            "name": "Michael Larsen",
            "salary": 60000,
            "age": 44,
            "title": "Developer",
            "email": ""
        }
        """;

        this.mockMvc
                .perform(
                        post("/api/v1/employee").contentType("application/json").content(employeeJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void delete_employees_successful() throws Exception {
        when(employeeController.deleteEmployeeById("ID"))
                .thenReturn(ResponseEntity.ok().build());

        this.mockMvc.perform(delete("/api/v1/employee/ID")).andDo(print()).andExpect(status().isOk());
    }
}
