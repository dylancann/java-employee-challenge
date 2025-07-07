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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.warn("API response empty or missing data for URL: {}", url);
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = getDataFromApi(
                BASE_URL,
                new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {
                }
        );
        if (employees == null || employees.isEmpty()) {
            log.warn("No employees found from API at URL: {}", BASE_URL);
        }
        return employees != null ? employees : Collections.emptyList();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> allEmployees = getAllEmployees();

        if (allEmployees == null || allEmployees.isEmpty()) {
            log.warn("Employee list is empty when searching for name '{}'", searchString);
            return Collections.emptyList();
        }

        String lowerSearch = searchString.toLowerCase();

        List<Employee> filtered = allEmployees.stream()
                .filter(emp -> emp.getEmployeeName() != null && emp.getEmployeeName().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            log.info("No employees found matching search string '{}'", searchString);
        }

        return filtered;
    }

    public Employee getEmployeeById(String id) {
        String url = BASE_URL + "/" + id;

        try {
            Employee employee = getDataFromApi(
                    url,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {
                    }
            );
            if (employee == null) {
                log.warn("Employee with ID {} not found.", id);
            }
            return employee;
        } catch (Exception e) {
            log.error("Error fetching employee with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public Integer getHighestSalary() {
        List<Employee> employees = getAllEmployees();

        return employees.stream()
                .mapToInt(Employee::getEmployeeSalary)
                .max()
                .orElse(0);
    }

}


