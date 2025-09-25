package com.walkingtree.salarytracker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    private String status;
    private String message;
    private Object data;
}
