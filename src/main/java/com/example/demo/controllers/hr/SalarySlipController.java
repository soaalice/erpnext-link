package com.example.demo.controllers.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.hr.SalarySlip;
import com.example.demo.services.PdfService;
import com.example.demo.services.hr.EmployeeService;
import com.example.demo.services.hr.SalarySlipService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hr/salary-slip")
public class SalarySlipController {
    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private EmployeeService employeeService;

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
            headers.add("Content-Disposition", "attachment; filename=" + name + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to export salary slip: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public String listSalarySlip(HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<SalarySlip> salarySlips = salarySlipService.getSalarySlips(sid);
            model.addAttribute("salarySlips", salarySlips);
            model.addAttribute("employees", employeeService.getEmployees(sid));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("salarySlips", List.of());
            model.addAttribute("error", "Unable to fetch salary slips: " + e.getMessage());
        }

        return "hr/salary-slips";
    }
}
