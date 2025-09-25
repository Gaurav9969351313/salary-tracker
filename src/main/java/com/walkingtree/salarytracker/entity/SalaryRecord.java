package com.walkingtree.salarytracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_salary_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String employeeId;
    
    @Column(nullable = false)
    private String financialYear;
    
    @Column(nullable = false)
    private String companyName;
    
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal fixedCTC;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal variableComponent;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal deductions;
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @Column(precision = 15, scale = 6)
    private BigDecimal exchangeRate; // Rate to USD
    
    @Column(precision = 15, scale = 2)
    private BigDecimal totalCTC;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal netSalary;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }
    
    private void calculateTotals() {
        if (fixedCTC != null) {
            totalCTC = fixedCTC;
            if (variableComponent != null) {
                totalCTC = totalCTC.add(variableComponent);
            }
            
            netSalary = totalCTC;
            if (deductions != null) {
                netSalary = netSalary.subtract(deductions);
            }
        }
    }
}