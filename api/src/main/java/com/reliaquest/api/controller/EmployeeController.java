package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees == null || employees.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        if (employees == null || employees.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalary();
        if (highestSalary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> top10Names = employeeService.getTop10HighestEarningEmployeeNames();
        return ResponseEntity.ok(top10Names);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeInput employeeInput) {
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        if (createdEmployee != null) {
            return ResponseEntity.ok(createdEmployee);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String name = employeeService.deleteEmployeeById(id);
        if (name != null) {
            return ResponseEntity.ok(name);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
