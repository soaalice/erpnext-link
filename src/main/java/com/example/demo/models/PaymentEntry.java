package com.example.demo.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEntry {
    @JsonProperty("naming_series")
    private String namingSeries = "ACC-PAY-.YYYY.-";
    @JsonProperty("payment_type")
    private String typeOfPayment;
    @JsonProperty("mode_of_payment")
    private String modeOfPayment;
    @JsonProperty("party_type")
    private String partyType = "Supplier";
    private String party;
    @JsonProperty("party_name")
    private String partyName;
    @JsonProperty("paid_from")
    private String paidFrom;
    @JsonProperty("paid_amount")
    private double paidAmount = 0.00;
    @JsonProperty("received_amount")
    private double receivedAmount = 0.00;
    @JsonProperty("posting_date")
    private String postingDate = LocalDate.now().toString();
    private String company = "Admin";
    private String referenceNo;
    private String referenceDate = LocalDate.now().toString();

    private double totalAmount = 0.00;
    private String name;

    private List<Object> references = new ArrayList<>();
}
