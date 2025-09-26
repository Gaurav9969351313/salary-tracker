package com.walkingtree.salarytracker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.walkingtree.salarytracker.entity.SalaryRecord;
import com.walkingtree.salarytracker.entity.dto.ApiResponseDto;
import com.walkingtree.salarytracker.entity.dto.CompanySalaryComparisonDto;
import com.walkingtree.salarytracker.entity.dto.CompanySwitchRequest;
import com.walkingtree.salarytracker.entity.dto.SalaryHikeRequest;
import com.walkingtree.salarytracker.entity.dto.SalaryRecordDto;
import com.walkingtree.salarytracker.entity.dto.SalaryTrendDto;
import com.walkingtree.salarytracker.service.SalaryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class SalaryController {
    
    @Autowired
    private SalaryService salaryService;
    
    @PostMapping("/upload")
    public Object uploadSalaryExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeeId") String employeeId,
            HttpServletRequest request, Model model) {
        boolean isApi = request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json");
        try {
            salaryService.uploadSalaryExcel(file, employeeId);
            if (isApi) {
                return ResponseEntity.ok(new ApiResponseDto("Success", "Excel uploaded and processed successfully", null));
            }
            model.addAttribute("uploadMessage", "Excel uploaded and processed successfully");
            List<SalaryTrendDto> trends = salaryService.getSalaryTrend(employeeId, "USD"); // By default using USD
            model.addAttribute("salaryTrendData", trends);
            return "admin/adminDashboard";
        } catch (IOException e) {
            if (isApi) {
                return ResponseEntity.badRequest().body(new ApiResponseDto("Error", "Failed to process Excel file: " + e.getMessage(), null));
            }
            model.addAttribute("uploadError", "Failed to process Excel file: " + e.getMessage());
            return "admin/adminDashboard";
        }
    }
    
    @GetMapping("/{year}")
    public ResponseEntity<?> getSalaryByYear(
            @PathVariable String year,
            @RequestParam String employeeId,
            @RequestParam(required = false) String baseCurrency) {
        Optional<SalaryRecordDto> salary = salaryService.getSalaryByYear(employeeId, year, baseCurrency);
        
        if (salary.isPresent()) {
            return ResponseEntity.ok(new ApiResponseDto("Success", "Salary details retrieved", salary.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/trend")
    public ResponseEntity<?> getSalaryTrend(
            @RequestParam String employeeId,
            @RequestParam(required = false, defaultValue = "USD") String baseCurrency) {
        List<SalaryTrendDto> trends = salaryService.getSalaryTrend(employeeId, baseCurrency);
        return ResponseEntity.ok(new ApiResponseDto("Success", "Salary trend data retrieved", trends));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestSalary(
            @RequestParam String employeeId,
            @RequestParam(required = false, defaultValue = "USD") String baseCurrency) {
        Optional<SalaryRecordDto> salary = salaryService.getLatestSalary(employeeId, baseCurrency);
        
        if (salary.isPresent()) {
            return ResponseEntity.ok(new ApiResponseDto("Success", "Latest salary retrieved", salary.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{year}/hike")
    public ResponseEntity<?> updateSalaryHike(
            @PathVariable String year,
            @RequestParam String employeeId,
            @Valid @RequestBody SalaryHikeRequest request) {
        Optional<SalaryRecordDto> updatedSalary = salaryService.updateSalaryHike(employeeId, year, request);
        
        if (updatedSalary.isPresent()) {
            return ResponseEntity.ok(new ApiResponseDto("Success", "Salary updated successfully", updatedSalary.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/switch")
    public ResponseEntity<?> addCompanySwitch(
            @RequestParam String employeeId,
            @Valid @RequestBody CompanySwitchRequest request) {
        SalaryRecordDto newRecord = salaryService.addCompanySwitch(employeeId, request);
        return ResponseEntity.ok(new ApiResponseDto("Success", "Company switch recorded successfully", newRecord));
    }
    
    @GetMapping("/compare")
    public ResponseEntity<?> compareSalaryAcrossCompanies(
            @RequestParam String employeeId,
            @RequestParam(required = false, defaultValue = "USD") String baseCurrency) {
        List<CompanySalaryComparisonDto> comparison = salaryService.compareSalaryAcrossCompanies(employeeId, baseCurrency);
        return ResponseEntity.ok(new ApiResponseDto("Success", "Company salary comparison retrieved", comparison));
    }
    
    // Additional utility endpoints
    @GetMapping("/all")
    public ResponseEntity<?> getAllSalaries(@RequestParam String employeeId) {
        // This would return all salary records for an employee
        List<SalaryRecordDto> allSalaries = salaryService.getAllSalaries(employeeId);
        return ResponseEntity.ok(new ApiResponseDto("Success", "All salary records retrieved", allSalaries));
    }

    @RequestMapping("/index")
	public String viewHomePage(Model model) {
	
		return "index";
	}
}