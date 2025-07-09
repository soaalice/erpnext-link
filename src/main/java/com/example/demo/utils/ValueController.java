package com.example.demo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ValueController {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    
    public static double parseValue(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            throw new Exception("Value cannot be null or empty");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format: " + value);
        }
    }

    public static String validateDate(String value) throws Exception{
        if (value == null || value.isEmpty()) {
            throw new Exception("Value cannot be null or empty");
        }
        try {
            LocalDate.parse(value, dateFormatter);
            return value;
        } catch (Exception e) {
            throw new Exception("Invalid date format: "+value);
        }
    }
}
