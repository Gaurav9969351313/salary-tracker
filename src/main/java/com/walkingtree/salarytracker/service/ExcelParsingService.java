package com.walkingtree.salarytracker.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.walkingtree.salarytracker.entity.SalaryRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParsingService {
    
    public List<SalaryRecord> parseExcelFile(MultipartFile file, String employeeId) throws IOException {
        List<SalaryRecord> salaryRecords = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                String sheetName = sheet.getSheetName();
                
                // Assume sheet name contains the financial year (e.g., "2023-24", "FY2023", etc.)
                String financialYear = extractFinancialYear(sheetName);
                
                SalaryRecord record = parseSheet(sheet, employeeId, financialYear);
                if (record != null) {
                    salaryRecords.add(record);
                }
            }
        }
        
        return salaryRecords;
    }
    
    private SalaryRecord parseSheet(Sheet sheet, String employeeId, String financialYear) {
        if (sheet.getPhysicalNumberOfRows() < 2) {
            return null;
        }
        
        SalaryRecord record = new SalaryRecord();
        record.setEmployeeId(employeeId);
        record.setFinancialYear(financialYear);
        
        // Expected format:
        // Row 0: Headers (Company, Fixed CTC, Variable, Deductions, Currency)
        // Row 1: Data
        
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);
        
        if (headerRow == null || dataRow == null) {
            return null;
        }
        
        try {
            // Parse based on column headers or positions
            record.setCompanyName(getCellStringValue(dataRow.getCell(0)));
            record.setFixedCTC(getCellBigDecimalValue(dataRow.getCell(1)));
            record.setVariableComponent(getCellBigDecimalValue(dataRow.getCell(2)));
            record.setDeductions(getCellBigDecimalValue(dataRow.getCell(3)));
            record.setCurrency(getCellStringValue(dataRow.getCell(4)));
            
            if (record.getCompanyName() == null || record.getFixedCTC() == null || record.getCurrency() == null) {
                return null;
            }
            
            return record;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractFinancialYear(String sheetName) {
        // Simple extraction - can be enhanced based on naming conventions
        if (sheetName.matches(".*\\d{4}.*")) {
            return sheetName;
        }
        return "Unknown";
    }
    
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }
    
    private BigDecimal getCellBigDecimalValue(Cell cell) {
        if (cell == null) return null;
        
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    if (value.isEmpty()) return null;
                    return new BigDecimal(value);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}