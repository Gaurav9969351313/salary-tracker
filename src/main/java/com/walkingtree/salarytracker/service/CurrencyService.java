package com.walkingtree.salarytracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walkingtree.salarytracker.entity.CurrencyRate;
import com.walkingtree.salarytracker.repository.CurrencyRateRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    
    private final CurrencyRateRepository currencyRateRepository;
    
    // Default rates (in production, this would come from an external API)
    private final Map<String, BigDecimal> defaultRates = new HashMap<String, BigDecimal>() {{
        put("USD", BigDecimal.ONE);
        put("INR", new BigDecimal("82.50"));
        put("AED", new BigDecimal("3.67"));
        put("EUR", new BigDecimal("0.85"));
        put("GBP", new BigDecimal("0.75"));
    }};
    
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        Optional<CurrencyRate> rateOpt = currencyRateRepository.findLatestRate(fromCurrency, toCurrency, LocalDate.now());
        
        if (rateOpt.isPresent()) {
            return rateOpt.get().getRate();
        }
        
        // Fallback to default rates (convert via USD)
        BigDecimal fromRate = defaultRates.getOrDefault(fromCurrency, BigDecimal.ONE);
        BigDecimal toRate = defaultRates.getOrDefault(toCurrency, BigDecimal.ONE);
        
        return toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
    }
    
    public void updateCurrencyRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setFromCurrency(fromCurrency);
        currencyRate.setToCurrency(toCurrency);
        currencyRate.setRate(rate);
        currencyRate.setRateDate(LocalDate.now());
        
        currencyRateRepository.save(currencyRate);
    }
}