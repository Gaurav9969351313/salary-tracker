package com.walkingtree.salarytracker.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySalaryComparisonDto {
    private String companyName;
    private BigDecimal averageSalary;
    private String currency;
    private int yearsWorked;
    private BigDecimal totalEarnings;
}