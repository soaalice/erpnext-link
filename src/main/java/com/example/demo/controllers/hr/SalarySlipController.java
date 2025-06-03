package com.example.demo.controllers.hr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.hr.SalarySlip;
import com.example.demo.services.PdfService;
import com.example.demo.services.hr.SalarySlipService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hr/salary-slip")
public class SalarySlipController {
    @Autowired
    private SalarySlipService salarySlipService;

    @PostMapping("/export")
    public ResponseEntity<?> exportSalarySlip(@RequestParam String name, HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired. Please log in again.");
        }

        try {
            SalarySlip salarySlip = salarySlipService.getSalarySlipByName(name, sid);
            byte[] pdfBytes = PdfService.exportSalarySlipToPdf(salarySlip);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=salary-slip-" + name + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to export salary slip: " + e.getMessage());
        }
    }
}
