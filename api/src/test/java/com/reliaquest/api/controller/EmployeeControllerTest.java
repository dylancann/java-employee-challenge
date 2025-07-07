package com.reliaquest.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllEmployees_returnsOkAndEmployees() throws Exception {
        Employee emp1 = new Employee();
        emp1.setEmployeeName("Dylan");
        Employee emp2 = new Employee();
        emp2.setEmployeeName("Carissa");

        when(employeeService.getAllEmployees()).thenReturn(List.of(emp1, emp2));

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("Dylan"))
                .andExpect(jsonPath("$[1].employee_name").value("Carissa"));
    }

    @Test
    void getAllEmployees_returnsNotFoundWhenEmpty() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/employee")).andExpect(status().isNotFound());
    }

    @Test
    void getEmployeeById_returnsOkWhenFound() throws Exception {
        Employee emp = new Employee();
        emp.setEmployeeName("Dylan");
        when(employeeService.getEmployeeById("123")).thenReturn(emp);

        mockMvc.perform(get("/api/v1/employee/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Dylan"));
    }

    @Test
    void getEmployeeById_returnsNotFoundWhenMissing() throws Exception {
        when(employeeService.getEmployeeById("123")).thenReturn(null);

        mockMvc.perform(get("/api/v1/employee/123")).andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_returnsOkWithCreatedEmployee() throws Exception {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Dylan Cann");

        Employee created = new Employee();
        created.setEmployeeName("Dylan Cann");

        when(employeeService.createEmployee(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Dylan Cann"));
    }

    @Test
    void createEmployee_returnsInternalServerErrorOnFail() throws Exception {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Dylan Cann");

        when(employeeService.createEmployee(any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteEmployeeById_returnsOkWhenDeleted() throws Exception {
        when(employeeService.deleteEmployeeById("123")).thenReturn("Dylan Cann");

        mockMvc.perform(delete("/api/v1/employee/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dylan Cann"));
    }

    @Test
    void deleteEmployeeById_returnsNotFoundWhenMissing() throws Exception {
        when(employeeService.deleteEmployeeById("123")).thenReturn(null);

        mockMvc.perform(delete("/api/v1/employee/123")).andExpect(status().isNotFound());
    }
}
