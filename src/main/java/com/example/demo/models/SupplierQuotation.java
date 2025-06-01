package com.example.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotation {
    private String name;
    private String status;
    private String supplier;
    private String total;
    @JsonProperty("total_qty")
    private String totalQty;
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    private List<SupplierQuotationItem> items;
}
