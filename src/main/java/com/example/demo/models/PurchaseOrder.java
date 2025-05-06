package com.example.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrder {
    private String name;
    private String supplier;
    private String status;
    @JsonProperty("advance_payment_status")
    private String advancedPaymentStatus;
    private String total;

    @JsonProperty("per_billed")
    private String perBilled;
    @JsonProperty("per_received")
    private String perReceived;

    List<PurchaseOrderItem> items;
}
