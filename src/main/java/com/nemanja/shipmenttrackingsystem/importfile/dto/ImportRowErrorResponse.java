package com.nemanja.shipmenttrackingsystem.importfile.dto;

public record ImportRowErrorResponse(
        int rowNumber,
        String message
) {
}