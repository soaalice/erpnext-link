package com.example.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotation {
    private String name;
    private String status;
    private String supplier;
    private String total;
    private List<SupplierQuotationItem> items;
}
