package com.example.demo.utils;

public class ValueController {
    
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
}
