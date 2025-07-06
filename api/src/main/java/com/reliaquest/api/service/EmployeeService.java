package com.reliaquest.api.service;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() {
        ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {}
        );

        ApiResponse<List<Employee>> apiResponse = response.getBody();

        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }
        return Collections.emptyList();
    }
}
