package com.example.demo.models;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotationItem {
    private String name;
    private String itemCode;
    private String itemName;
    private double qty;
    private double rate;
}
