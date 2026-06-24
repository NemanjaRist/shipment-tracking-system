package com.nemanja.shipmenttrackingsystem.importfile.dto;

public record ShipmentImportRow(
        int rowNumber,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String customerPhone,
        String contentDescription,
        String note
) {
}