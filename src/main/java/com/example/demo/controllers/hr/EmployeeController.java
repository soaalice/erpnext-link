package com.example.demo.controllers.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.hr.Employee;
import com.example.demo.services.hr.EmployeeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hr/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping("/list")
    public String list(HttpSession session,Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<Employee> employees = employeeService.getEmployees(sid);
            model.addAttribute("employees", employees);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch employees: " + e.getMessage());
        }
        return "hr/employees";
    }
}
