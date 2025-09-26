package com.walkingtree.salarytracker.entity.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanySwitchRequest {
    @NotBlank
    private String newCompanyName;
    
    @NotBlank
    private String financialYear;
    
    @DecimalMin(value = "0.0")
    private BigDecimal fixedCTC;
    
    private BigDecimal variableComponent;
    private BigDecimal deductions;
    
    @NotBlank
    private String currency;
    
}