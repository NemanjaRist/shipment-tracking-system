package com.nemanja.shipmenttrackingsystem.importfile;

import com.nemanja.shipmenttrackingsystem.importfile.dto.ShipmentImportRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvShipmentParser {

    public List<ShipmentImportRow> parse(MultipartFile file) {
        List<ShipmentImportRow> rows = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
                );
                CSVParser csvParser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .get()
                        .parse(reader)
        ) {
            for (CSVRecord record : csvParser) {
                rows.add(new ShipmentImportRow(
                        (int) record.getRecordNumber() + 1,
                        getValue(record, "customerFirstName"),
                        getValue(record, "customerLastName"),
                        getValue(record, "customerEmail"),
                        getValue(record, "customerPhone"),
                        getValue(record, "contentDescription"),
                        getValue(record, "note")
                ));
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse CSV file: " + ex.getMessage());
        }

        return rows;
    }

    private String getValue(CSVRecord record, String columnName) {
        if (!record.isMapped(columnName)) {
            return null;
        }

        String value = record.get(columnName);
        return value == null || value.isBlank() ? null : value.trim();
    }
}