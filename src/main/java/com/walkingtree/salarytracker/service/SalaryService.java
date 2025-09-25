package com.walkingtree.salarytracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.walkingtree.salarytracker.entity.SalaryRecord;
import com.walkingtree.salarytracker.entity.dto.CompanySalaryComparisonDto;
import com.walkingtree.salarytracker.entity.dto.CompanySwitchRequest;
import com.walkingtree.salarytracker.entity.dto.SalaryHikeRequest;
import com.walkingtree.salarytracker.entity.dto.SalaryRecordDto;
import com.walkingtree.salarytracker.entity.dto.SalaryTrendDto;
import com.walkingtree.salarytracker.repository.SalaryRecordRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private ExcelParsingService excelParsingService;

    @Autowired
    private CurrencyService currencyService;

    public List<SalaryRecord> uploadSalaryExcel(MultipartFile file, String employeeId) throws IOException {
        List<SalaryRecord> records = excelParsingService.parseExcelFile(file, employeeId);

        for (SalaryRecord record : records) {
            Optional<SalaryRecord> existing = salaryRecordRepository
                    .findByEmployeeIdAndFinancialYear(employeeId, record.getFinancialYear());

            if (existing.isPresent()) {
                SalaryRecord existingRecord = existing.get();
                existingRecord.setCompanyName(record.getCompanyName());
                existingRecord.setFixedCTC(record.getFixedCTC());
                existingRecord.setVariableComponent(record.getVariableComponent());
                existingRecord.setDeductions(record.getDeductions());
                existingRecord.setCurrency(record.getCurrency());
                salaryRecordRepository.save(existingRecord);
            } else {
                salaryRecordRepository.save(record);
            }
        }

        return records;
    }

    public Optional<SalaryRecordDto> getSalaryByYear(String employeeId, String year, String baseCurrency) {
        Optional<SalaryRecord> recordOpt = salaryRecordRepository.findByEmployeeIdAndFinancialYear(employeeId, year);

        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        SalaryRecord record = recordOpt.get();
        SalaryRecordDto dto = convertToDto(record);

        if (baseCurrency != null && !baseCurrency.equals(record.getCurrency())) {
            BigDecimal convertedCTC = currencyService.convertCurrency(
                    record.getTotalCTC(), record.getCurrency(), baseCurrency);
            dto.setConvertedTotalCTC(convertedCTC);
            dto.setConvertedCurrency(baseCurrency);
        }

        return Optional.of(dto);
    }

    public List<SalaryTrendDto> getSalaryTrend(String employeeId, String baseCurrency) {
        List<SalaryRecord> records = salaryRecordRepository.findByEmployeeIdOrderByFinancialYearDesc(employeeId);

        return records.stream()
                .map(record -> {
                    BigDecimal ctc = record.getTotalCTC();
                    String currency = record.getCurrency();

                    if (baseCurrency != null && !baseCurrency.equals(record.getCurrency())) {
                        ctc = currencyService.convertCurrency(ctc, record.getCurrency(), baseCurrency);
                        currency = baseCurrency;
                    }

                    return new SalaryTrendDto(record.getFinancialYear(), ctc, currency, record.getCompanyName());
                })
                .collect(Collectors.toList());
    }

    public Optional<SalaryRecordDto> getLatestSalary(String employeeId, String baseCurrency) {
        Optional<SalaryRecord> recordOpt = salaryRecordRepository
                .findTopByEmployeeIdOrderByFinancialYearDesc(employeeId);

        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        SalaryRecord record = recordOpt.get();
        SalaryRecordDto dto = convertToDto(record);

        if (baseCurrency != null && !baseCurrency.equals(record.getCurrency())) {
            BigDecimal convertedCTC = currencyService.convertCurrency(
                    record.getTotalCTC(), record.getCurrency(), baseCurrency);
            dto.setConvertedTotalCTC(convertedCTC);
            dto.setConvertedCurrency(baseCurrency);
        }

        return Optional.of(dto);
    }

    public Optional<SalaryRecordDto> updateSalaryHike(String employeeId, String year, SalaryHikeRequest request) {
        Optional<SalaryRecord> recordOpt = salaryRecordRepository.findByEmployeeIdAndFinancialYear(employeeId, year);

        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        SalaryRecord record = recordOpt.get();

        if (request.getNewFixedCTC() != null) {
            record.setFixedCTC(request.getNewFixedCTC());
        } else if (request.getHikePercentage() != null) {
            BigDecimal hikeMultiplier = BigDecimal.ONE.add(request.getHikePercentage().divide(BigDecimal.valueOf(100)));
            record.setFixedCTC(record.getFixedCTC().multiply(hikeMultiplier).setScale(2, RoundingMode.HALF_UP));
        }

        if (request.getNewVariableComponent() != null) {
            record.setVariableComponent(request.getNewVariableComponent());
        }

        SalaryRecord savedRecord = salaryRecordRepository.save(record);
        return Optional.of(convertToDto(savedRecord));
    }

    public SalaryRecordDto addCompanySwitch(String employeeId, CompanySwitchRequest request) {
        SalaryRecord record = new SalaryRecord();
        record.setEmployeeId(employeeId);
        record.setCompanyName(request.getNewCompanyName());
        record.setFinancialYear(request.getFinancialYear());
        record.setFixedCTC(request.getFixedCTC());
        record.setVariableComponent(request.getVariableComponent());
        record.setDeductions(request.getDeductions());
        record.setCurrency(request.getCurrency());

        SalaryRecord savedRecord = salaryRecordRepository.save(record);
        return convertToDto(savedRecord);
    }

    public List<CompanySalaryComparisonDto> compareSalaryAcrossCompanies(String employeeId, String baseCurrency) {
        List<String> companies = salaryRecordRepository.findDistinctCompaniesByEmployeeId(employeeId);

        return companies.stream()
                .map(company -> {
                    List<SalaryRecord> companyRecords = salaryRecordRepository
                            .findByEmployeeIdAndCompanyName(employeeId, company);

                    BigDecimal totalEarnings = BigDecimal.ZERO;
                    BigDecimal convertedTotalEarnings = BigDecimal.ZERO;

                    for (SalaryRecord record : companyRecords) {
                        BigDecimal ctc = record.getTotalCTC();
                        totalEarnings = totalEarnings.add(ctc);

                        BigDecimal convertedCTC = currencyService.convertCurrency(ctc, record.getCurrency(),
                                baseCurrency);
                        convertedTotalEarnings = convertedTotalEarnings.add(convertedCTC);
                    }

                    BigDecimal averageSalary = convertedTotalEarnings.divide(
                            BigDecimal.valueOf(companyRecords.size()), 2, RoundingMode.HALF_UP);

                    return new CompanySalaryComparisonDto(
                            company, averageSalary, baseCurrency, companyRecords.size(), convertedTotalEarnings);
                })
                .collect(Collectors.toList());
    }

    public List<SalaryRecordDto> getAllSalaries(String employeeId) {
        List<SalaryRecord> records = salaryRecordRepository.findByEmployeeIdOrderByFinancialYearDesc(employeeId);
        return records.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private SalaryRecordDto convertToDto(SalaryRecord record) {
        SalaryRecordDto dto = new SalaryRecordDto();
        dto.setId(record.getId());
        dto.setEmployeeId(record.getEmployeeId());
        dto.setFinancialYear(record.getFinancialYear());
        dto.setCompanyName(record.getCompanyName());
        dto.setFixedCTC(record.getFixedCTC());
        dto.setVariableComponent(record.getVariableComponent());
        dto.setDeductions(record.getDeductions());
        dto.setCurrency(record.getCurrency());
        dto.setTotalCTC(record.getTotalCTC());
        dto.setNetSalary(record.getNetSalary());
        return dto;
    }
}