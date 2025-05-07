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
    private String warehouse;
    private double qty;
    private double rate;

    @JsonProperty("material_request")
    private String materialRequest;
    @JsonProperty("material_request_item")
    private String materialRequestItem;
    @JsonProperty("request_for_quotation")
    private String requestForQuotation;
    @JsonProperty("request_for_quotation_item")
    private String requestForQuotationItem;

}
