package com.reliaquest.api.model;

import lombok.Getter;

@Getter
public class CreateEmployeeInput {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
}
