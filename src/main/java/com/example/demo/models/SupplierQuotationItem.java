package com.example.demo.models;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotationItem {
    private String name;
    @com.fasterxml.jackson.annotation.JsonProperty("item_code")
    private String itemCode;
    @com.fasterxml.jackson.annotation.JsonProperty("item_name")
    private String itemName;
    private double qty;
    private double rate;
}
