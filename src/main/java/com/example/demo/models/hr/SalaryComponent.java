package com.example.demo.models.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryComponent {
    private String name;
    @JsonProperty("salary_component")
    private String salaryComponent;
    private String type; // e.g., "Earnings", "Deductions"
    private double amount;
}
