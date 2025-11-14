package com.onlineshop.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.test.dto.request.EmployeeRequest;
import com.onlineshop.test.dto.response.EmployeeResponse;
import com.onlineshop.test.exception.EmployeeNotFoundException;
import com.onlineshop.test.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EmployeeService employeeService;

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees_WhenEmployeesExist() throws Exception {
        EmployeeResponse employeeResponse1 = new EmployeeResponse(1L, "Islam Makhachev", "Lightweight Champion", 500000L, "LW", "Khabib");

        EmployeeResponse employeeResponse2 = new EmployeeResponse(2L, "Alexander Volkanovski", "Featherweight Champion", 450000L, "FW", "None");

        when(employeeService.getAllEmployees()).thenReturn(List.of(employeeResponse1, employeeResponse2));

        mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenNoEmployeesExist() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() throws Exception {
        EmployeeResponse employeeResponse = new EmployeeResponse(1L, "Khabib Nurmagomedov", "Coach", 700000L, "AKA", "None");

        when(employeeService.getEmployeeById(1L)).thenReturn(employeeResponse);

        mockMvc.perform(get("/api/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Khabib Nurmagomedov"));
    }

    @Test
    void getEmployeeById_ShouldReturnNotFound_WhenEmployeeDoesNotExist() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/api/employees/99")).andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_ShouldCreateEmployee_WhenRequestIsValid() throws Exception {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName("Justin Gaethje");
        employeeRequest.setPosition("Top Lightweight");
        employeeRequest.setSalary(300000L);
        employeeRequest.setDepartmentId(1L);

        EmployeeResponse employeeResponse = new EmployeeResponse(1L, "Justin Gaethje", "Top Lightweight", 300000L, "LW", "None");

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employeeResponse);

        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Justin Gaethje"));
    }

    @Test
    void createEmployee_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        EmployeeRequest invalidEmployeeRequest = new EmployeeRequest();
        invalidEmployeeRequest.setName("");
        invalidEmployeeRequest.setSalary(-1L);

        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidEmployeeRequest))).andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_ShouldUpdateEmployee_WhenEmployeeExists() throws Exception {
        EmployeeRequest updateRequest = new EmployeeRequest();
        updateRequest.setName("Charles Oliveira");
        updateRequest.setPosition("Lightweight");
        updateRequest.setSalary(350000L);
        updateRequest.setDepartmentId(1L);

        EmployeeResponse updatedResponse = new EmployeeResponse(1L, "Charles Oliveira", "Lightweight", 350000L, "LW", "None");

        when(employeeService.updateEmployee(Mockito.eq(1L), any(EmployeeRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Charles Oliveira"));
    }

    @Test
    void updateEmployee_ShouldReturnNotFound_WhenEmployeeDoesNotExist() throws Exception {
        EmployeeRequest updateRequest = new EmployeeRequest();
        updateRequest.setName("Unknown Fighter");
        updateRequest.setSalary(1000L);
        updateRequest.setDepartmentId(1L);

        when(employeeService.updateEmployee(Mockito.eq(55L), any(EmployeeRequest.class))).thenThrow(new EmployeeNotFoundException(55L));

        mockMvc.perform(put("/api/employees/55").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployee_WhenEmployeeExists() throws Exception {
        mockMvc.perform(delete("/api/employees/1")).andExpect(status().isOk());

        Mockito.verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_ShouldReturnNotFound_WhenEmployeeDoesNotExist() throws Exception {
        doThrow(new EmployeeNotFoundException(77L)).when(employeeService).deleteEmployee(77L);

        mockMvc.perform(delete("/api/employees/77")).andExpect(status().isNotFound());
    }
}
