package com.example.demo.models;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierQuotationItem {
    private String name;
    @JsonProperty("item_code")
    private String itemCode;
    @JsonProperty("item_name")
    private String itemName;
    private double qty;
    private double rate;
}
