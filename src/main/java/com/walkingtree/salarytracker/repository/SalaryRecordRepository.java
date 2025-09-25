package com.walkingtree.salarytracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.walkingtree.salarytracker.entity.SalaryRecord;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    
    List<SalaryRecord> findByEmployeeIdOrderByFinancialYearDesc(String employeeId);
    
    Optional<SalaryRecord> findByEmployeeIdAndFinancialYear(String employeeId, String financialYear);
    
    Optional<SalaryRecord> findTopByEmployeeIdOrderByFinancialYearDesc(String employeeId);
    
    @Query("SELECT DISTINCT s.companyName FROM SalaryRecord s WHERE s.employeeId = :employeeId")
    List<String> findDistinctCompaniesByEmployeeId(@Param("employeeId") String employeeId);
    
    @Query("SELECT s FROM SalaryRecord s WHERE s.employeeId = :employeeId AND s.companyName = :companyName ORDER BY s.financialYear DESC")
    List<SalaryRecord> findByEmployeeIdAndCompanyName(@Param("employeeId") String employeeId, @Param("companyName") String companyName);
}