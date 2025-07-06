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
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private <T> T getDataFromApi(String url, ParameterizedTypeReference<ApiResponse<T>> responseType) {
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        );
        ApiResponse<T> apiResponse = response.getBody();
        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        return getDataFromApi(
                BASE_URL,
                new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {}
        );
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> allEmployees = getAllEmployees();

        if (allEmployees == null || allEmployees.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerSearch = searchString.toLowerCase();

        return allEmployees.stream()
                .filter(emp -> emp.getEmployeeName() != null && emp.getEmployeeName().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
    }
}

