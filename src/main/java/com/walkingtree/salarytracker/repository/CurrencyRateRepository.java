package com.walkingtree.salarytracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.walkingtree.salarytracker.entity.CurrencyRate;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    
    @Query("SELECT c FROM CurrencyRate c WHERE c.fromCurrency = :from AND c.toCurrency = :to AND c.rateDate <= :date ORDER BY c.rateDate DESC LIMIT 1")
    Optional<CurrencyRate> findLatestRate(@Param("from") String fromCurrency, 
                                         @Param("to") String toCurrency, 
                                         @Param("date") LocalDate date);
}