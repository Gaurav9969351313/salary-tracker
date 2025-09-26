package com.walkingtree.salarytracker.entity.dto;

import lombok.Data;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
public class SalaryHikeRequest {
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal hikePercentage;
    private BigDecimal newVariableComponentHikePercentage;
}