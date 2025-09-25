package com.walkingtree.salarytracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_currency_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 3)
    private String fromCurrency;
    
    @Column(nullable = false, length = 3)
    private String toCurrency;
    
    @Column(precision = 15, scale = 6, nullable = false)
    private BigDecimal rate;
    
    @Column(nullable = false)
    private LocalDate rateDate;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}