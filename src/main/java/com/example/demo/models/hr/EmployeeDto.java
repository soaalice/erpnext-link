package com.example.demo.models.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDto {
    private Integer ref;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String gender;
    @JsonProperty("hire_date")
    private String hireDate;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    private String company;
    private String name;
}