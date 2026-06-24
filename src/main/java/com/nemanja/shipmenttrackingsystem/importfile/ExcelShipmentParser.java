package com.nemanja.shipmenttrackingsystem.importfile;

import com.nemanja.shipmenttrackingsystem.importfile.dto.ShipmentImportRow;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelShipmentParser {

    public List<ShipmentImportRow> parse(MultipartFile file) {
        List<ShipmentImportRow> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                rows.add(new ShipmentImportRow(
                        rowIndex + 1,
                        getCellValue(row.getCell(0)),
                        getCellValue(row.getCell(1)),
                        getCellValue(row.getCell(2)),
                        getCellValue(row.getCell(3)),
                        getCellValue(row.getCell(4)),
                        getCellValue(row.getCell(5))
                ));
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse Excel file: " + ex.getMessage());
        }

        return rows;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < 6; cellIndex++) {
            if (getCellValue(row.getCell(cellIndex)) != null) {
                return false;
            }
        }

        return true;
    }


}