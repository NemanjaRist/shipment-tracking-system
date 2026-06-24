package com.nemanja.shipmenttrackingsystem.importfile.dto;

import java.util.List;

public record ImportResultResponse(
        int totalRows,
        int importedRows,
        int failedRows,
        List<ImportRowErrorResponse> errors
) {
}