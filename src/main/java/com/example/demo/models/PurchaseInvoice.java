package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseInvoice {
    private String name;
    private String status;
    private String supplier;
    private String total;
}
