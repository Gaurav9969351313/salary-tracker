package com.walkingtree.salarytracker.entity.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanySwitchRequest {
    @NotBlank
    private String newCompanyName;
    
    @NotBlank
    private String financialYear;
    
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal fixedCTC;
    
    private BigDecimal variableComponent;
    private BigDecimal deductions;
    
    @NotBlank
    private String currency;
    
    private String notes;
}