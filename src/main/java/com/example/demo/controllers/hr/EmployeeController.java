package com.example.demo.controllers.hr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.hr.Employee;
import com.example.demo.models.hr.SalarySlip;
import com.example.demo.services.hr.EmployeeService;
import com.example.demo.services.hr.SalarySlipService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hr/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalarySlipService salarySlipService;
    
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

    @GetMapping("/{id}")
    public String getEmployee(@PathVariable String id, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            Employee employee = employeeService.getEmployee(sid, id);
            model.addAttribute("employee", employee);
            List<SalarySlip> salarySlips = salarySlipService.getSalarySlipsByEmployee(employee.getName(), sid);
            model.addAttribute("salarySlips", salarySlips);
        } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", e.getMessage());
           return "redirect:/hr/employee/list";
        }

        return "hr/employee-details";
    }
}
