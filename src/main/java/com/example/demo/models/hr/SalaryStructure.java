package com.example.demo.models.hr;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryStructure {
    private String name;
    private String company;
    private List<SalaryComponent> salaryComponents;
}
