package com.walkingtree.salarytracker.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecordDto {
    private Long id;
    private String employeeId;
    private String financialYear;
    private String companyName;
    private BigDecimal fixedCTC;
    private BigDecimal variableComponent;
    private BigDecimal deductions;
    private String currency;
    private BigDecimal totalCTC;
    private BigDecimal netSalary;
    private BigDecimal convertedTotalCTC;
    private String convertedCurrency;
}