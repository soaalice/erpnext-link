package com.example.demo.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseInvoice {
    private String name;
    private String status;
    private String supplier;
    private String total;
    @JsonProperty("outstanding_amount")
    private double outstandingAmount = 0.00;

    private String company = "Admin";
    @JsonProperty("posting_date")
    private String postingDate;
    @JsonProperty("posting_time")
    private String postingTime;
    @JsonProperty("due_date")
    private String dueDate;
    @JsonProperty("total_qty")
    private double totalQty = 0.00;
    private String remarks;
    List<PurchaseInvoiceItem> items = new ArrayList<>();

}
