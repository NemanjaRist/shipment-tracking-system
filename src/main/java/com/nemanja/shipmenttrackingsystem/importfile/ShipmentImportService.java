package com.nemanja.shipmenttrackingsystem.importfile;

import com.nemanja.shipmenttrackingsystem.customer.Customer;
import com.nemanja.shipmenttrackingsystem.customer.CustomerService;
import com.nemanja.shipmenttrackingsystem.importfile.dto.ImportResultResponse;
import com.nemanja.shipmenttrackingsystem.importfile.dto.ImportRowErrorResponse;
import com.nemanja.shipmenttrackingsystem.importfile.dto.ShipmentImportRow;
import com.nemanja.shipmenttrackingsystem.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentImportService {

    private final CsvShipmentParser csvShipmentParser;
    private final ExcelShipmentParser excelShipmentParser;
    private final CustomerService customerService;
    private final ShipmentService shipmentService;

    @Transactional
    public ImportResultResponse importShipments(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import file is required");
        }

        List<ShipmentImportRow> rows = parseFile(file);

        int importedRows = 0;
        List<ImportRowErrorResponse> errors = new ArrayList<>();

        for (ShipmentImportRow row : rows) {
            try {
                validateRow(row);

                Customer customer = customerService.findOrCreateCustomer(
                        row.customerFirstName(),
                        row.customerLastName(),
                        row.customerEmail(),
                        row.customerPhone()
                );

                shipmentService.createShipmentFromImport(
                        customer,
                        row.contentDescription(),
                        row.note()
                );

                importedRows++;
            } catch (Exception ex) {
                errors.add(new ImportRowErrorResponse(
                        row.rowNumber(),
                        ex.getMessage()
                ));
            }
        }

        return new ImportResultResponse(
                rows.size(),
                importedRows,
                errors.size(),
                errors
        );
    }

    private List<ShipmentImportRow> parseFile(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("File name is missing");
        }

        String lowerCaseFilename = filename.toLowerCase();

        if (lowerCaseFilename.endsWith(".csv")) {
            return csvShipmentParser.parse(file);
        }

        if (lowerCaseFilename.endsWith(".xlsx")) {
            return excelShipmentParser.parse(file);
        }

        throw new IllegalArgumentException("Unsupported file format. Only .csv and .xlsx files are supported.");
    }

    private void validateRow(ShipmentImportRow row) {
        if (isBlank(row.customerFirstName())) {
            throw new IllegalArgumentException("Customer first name is required");
        }

        if (isBlank(row.customerLastName())) {
            throw new IllegalArgumentException("Customer last name is required");
        }

        if (isBlank(row.customerEmail())) {
            throw new IllegalArgumentException("Customer email is required");
        }

        if (!row.customerEmail().contains("@")) {
            throw new IllegalArgumentException("Customer email must be valid");
        }

        if (isBlank(row.contentDescription())) {
            throw new IllegalArgumentException("Content description is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}