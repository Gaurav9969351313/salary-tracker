package com.walkingtree.salarytracker.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryTrendDto {
    private String year;
    private BigDecimal ctc;
    private String currency;
    private String companyName;
}
