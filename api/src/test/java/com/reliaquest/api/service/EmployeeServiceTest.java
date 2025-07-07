package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee();
        sampleEmployee.setId(UUID.randomUUID().toString());
        sampleEmployee.setEmployeeName("Dylan Cann");
        sampleEmployee.setEmployeeSalary(100000);
        sampleEmployee.setEmployeeAge(30);
        sampleEmployee.setEmployeeTitle("Developer");
        sampleEmployee.setEmployeeEmail("john@example.com");
    }

    @Test
    void getAllEmployees_returnsList() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(List.of(sampleEmployee));

        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(BASE_URL), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("Dylan Cann", employees.get(0).getEmployeeName());
    }

    @Test
    void getEmployeesByNameSearch_filtersCorrectly() {
        Employee sampleEmployee = new Employee();
        sampleEmployee.setEmployeeName("John Doe");

        EmployeeService spyService = Mockito.spy(employeeService);

        Mockito.doReturn(List.of(sampleEmployee)).when(spyService).getAllEmployees();

        List<Employee> filtered = spyService.getEmployeesByNameSearch("john");
        assertEquals(1, filtered.size());

        filtered = spyService.getEmployeesByNameSearch("nonexistent");
        assertEquals(0, filtered.size());
    }

    @Test
    void getEmployeeById_returnsEmployee() {
        ApiResponse<Employee> apiResponse = new ApiResponse<>();
        apiResponse.setData(sampleEmployee);
        ResponseEntity<ApiResponse<Employee>> responseEntity = ResponseEntity.ok(apiResponse);

        String url = BASE_URL + "/" + sampleEmployee.getId();

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Employee employee = employeeService.getEmployeeById(sampleEmployee.getId());
        assertNotNull(employee);
        assertEquals("Dylan Cann", employee.getEmployeeName());
    }

    @Test
    void getHighestSalary_returnsMaxSalary() {
        ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
        apiResponse.setData(List.of(sampleEmployee));
        ResponseEntity<ApiResponse<List<Employee>>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(eq(BASE_URL), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        int maxSalary = employeeService.getHighestSalary();

        assertEquals(sampleEmployee.getEmployeeSalary(), maxSalary);
    }

    @Test
    void getTop10HighestEarningEmployeeNames_returnsSortedList() {
        Employee emp1 = new Employee();
        emp1.setEmployeeName("Dylan Cann");
        emp1.setEmployeeSalary(100000);

        Employee emp2 = new Employee();
        emp2.setEmployeeName("Carissa Beebe");
        emp2.setEmployeeSalary(120000);

        EmployeeService spyService = Mockito.spy(employeeService);

        Mockito.doReturn(List.of(emp1, emp2)).when(spyService).getAllEmployees();

        List<String> top10 = spyService.getTop10HighestEarningEmployeeNames();

        assertEquals(2, top10.size());
        assertEquals("Carissa Beebe", top10.get(0));
        assertEquals("Dylan Cann", top10.get(1));
    }

    @Test
    void createEmployee_successfulCreation() {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Dylan Cann");

        ApiResponse<Employee> apiResponse = new ApiResponse<>();
        apiResponse.setData(sampleEmployee);

        ResponseEntity<ApiResponse<Employee>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                        eq(BASE_URL),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Employee created = employeeService.createEmployee(input);

        assertNotNull(created);
        assertEquals("Dylan Cann", created.getEmployeeName());
    }

    @Test
    void createEmployee_failureReturnsNull() {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Dylan Cann");

        ResponseEntity<ApiResponse<Employee>> responseEntity = ResponseEntity.ok(new ApiResponse<>());

        when(restTemplate.exchange(
                        eq(BASE_URL),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Employee created = employeeService.createEmployee(input);
        assertNull(created);
    }

    @Test
    void deleteEmployeeById_successfulDelete() {
        EmployeeService spyService = Mockito.spy(employeeService);

        Mockito.doReturn(sampleEmployee).when(spyService).getEmployeeById(sampleEmployee.getId());

        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setData(true);
        ResponseEntity<ApiResponse<Boolean>> responseEntity = ResponseEntity.ok(apiResponse);

        Mockito.when(restTemplate.exchange(
                        eq(BASE_URL),
                        eq(HttpMethod.DELETE),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        String deletedName = spyService.deleteEmployeeById(sampleEmployee.getId());

        assertEquals(sampleEmployee.getEmployeeName(), deletedName);
    }

    @Test
    void deleteEmployeeById_employeeNotFound_returnsNull() {
        EmployeeService spyService = Mockito.spy(employeeService);

        Mockito.doReturn(null).when(spyService).getEmployeeById(anyString());

        String result = spyService.deleteEmployeeById("some-id");

        assertNull(result);
    }

    @Test
    void deleteEmployeeById_deleteFails_returnsNull() {
        ApiResponse<Employee> getEmployeeResponse = new ApiResponse<>();
        getEmployeeResponse.setData(sampleEmployee);
        ResponseEntity<ApiResponse<Employee>> getResponseEntity = ResponseEntity.ok(getEmployeeResponse);

        when(restTemplate.exchange(
                        eq(BASE_URL + "/" + sampleEmployee.getId()),
                        eq(HttpMethod.GET),
                        isNull(),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(getResponseEntity);

        ApiResponse<Boolean> deleteApiResponse = new ApiResponse<>();
        deleteApiResponse.setData(false);
        ResponseEntity<ApiResponse<Boolean>> deleteResponseEntity = ResponseEntity.ok(deleteApiResponse);

        when(restTemplate.exchange(
                        eq(BASE_URL),
                        eq(HttpMethod.DELETE),
                        any(HttpEntity.class),
                        any(ParameterizedTypeReference.class)))
                .thenReturn(deleteResponseEntity);

        String deletedName = employeeService.deleteEmployeeById(sampleEmployee.getId());

        assertNull(deletedName);
    }
}
