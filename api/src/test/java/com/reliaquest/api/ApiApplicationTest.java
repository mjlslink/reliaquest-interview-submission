package com.reliaquest.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .thenReturn(ResponseEntity.ok(List.of(new Employee(
                        UUID.randomUUID(), "Michael Larsen", 60000, 44, "Developer", "mjlslink@yahoo.com"))));

        this.mockMvc.perform(get("/api/v1/employee")).andDo(print()).andExpect(status().isOk());
    }
}
