package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseInvoiceItem {
    @JsonProperty("item_code")
    private String itemCode;
    @JsonProperty("item_name")
    private String itemName;
    private double qty = 0.00;
    @JsonProperty("received_qty")
    private double receivedQty = 0.00;
    private double rate = 0.00;
    private double amount = 0.00;
    private String uom;
    private String warehouse;
    private String description;
    @JsonProperty("cost_center")
    private String costCenter;
    @JsonProperty("purchase_order")
    private String purchaseOrder;
    @JsonProperty("material_request")
    private String materialRequest;
}
