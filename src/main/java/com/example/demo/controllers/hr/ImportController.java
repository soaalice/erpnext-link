package com.example.demo.controllers.hr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.services.ImportService;
import com.example.demo.services.ImportService.ImportException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hr")
public class ImportController {

    @Autowired
    private ImportService importService;
    
    @GetMapping("/import")
    public String getImportPage(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }
        return "hr/import-form";
    }

    @PostMapping("/import")
    public String importData(@RequestParam("employeeCsv") MultipartFile employeeCsv,
                              @RequestParam("salaryStructureCsv") MultipartFile salaryStructureCsv,
                              @RequestParam("assignmentCsv") MultipartFile assignmentCsv,
                              RedirectAttributes redirectAttributes, HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }
        try {
            importService.import_data(employeeCsv, salaryStructureCsv, assignmentCsv, session);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("success", "Successfully imported data!");
        return "redirect:/hr/import";
    }

}
