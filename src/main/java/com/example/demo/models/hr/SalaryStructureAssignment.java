package com.example.demo.models.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryStructureAssignment {
    private String from_date;
    private Integer employee_ref;
    private Double base;
    private String salary_structure;
    private String company;
}