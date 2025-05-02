package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrder {
    private String name;
    private String supplier;
    private String status;
    private String total;
}
