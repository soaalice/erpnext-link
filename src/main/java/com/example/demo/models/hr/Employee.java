package com.example.demo.models.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
    private String name;
    @JsonProperty("employee_name")
    private String employeeName;
    private String gender;
    private String company;
    private double ctc;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    @JsonProperty("date_of_joining")
    private String dateOfJoining;
}
