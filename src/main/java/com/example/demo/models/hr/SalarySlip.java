package com.example.demo.models.hr;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalarySlip {
    private String name;
    @JsonProperty("employee_name")
    private String employeeName;
    @JsonProperty("total_working_days")
    private double totalWorkingDays;
    @JsonProperty("absent_days")
    private double absentDays;
    @JsonProperty("gross_pay")
    private double grossPay;
    @JsonProperty("total_deduction")
    private double totalDeduction;
    @JsonProperty("net_pay")
    private double netPay;
    @JsonProperty("total_in_words")
    private String totalInWords;
    @JsonProperty("total_incoming_tax")
    private double totalIncomingTax;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("posting_date")
    private String postingDate;

    List<SalaryComponent> earnings;
    List<SalaryComponent> deductions;
}
