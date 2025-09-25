package com.walkingtree.salarytracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.walkingtree.salarytracker.entity.dto.ApiResponseDto;
import com.walkingtree.salarytracker.service.CurrencyService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        BigDecimal convertedAmount = currencyService.convertCurrency(amount, from, to);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalAmount", amount);
        result.put("originalCurrency", from);
        result.put("convertedAmount", convertedAmount);
        result.put("convertedCurrency", to);
        result.put("exchangeRate", currencyService.getExchangeRate(from, to));
        
        return ResponseEntity.ok(new ApiResponseDto("Success", "Currency converted successfully", result));
    }
    
    @GetMapping("/rate")
    public ResponseEntity<?> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        BigDecimal rate = currencyService.getExchangeRate(from, to);
        
        Map<String, Object> result = new HashMap<>();
        result.put("fromCurrency", from);
        result.put("toCurrency", to);
        result.put("rate", rate);
        
        return ResponseEntity.ok(new ApiResponseDto("Success", "Exchange rate retrieved", result));
    }
    
    @PostMapping("/rate")
    public ResponseEntity<?> updateExchangeRate(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal rate) {
        currencyService.updateCurrencyRate(from, to, rate);
        return ResponseEntity.ok(new ApiResponseDto("Success", "Exchange rate updated successfully", null));
    }
}