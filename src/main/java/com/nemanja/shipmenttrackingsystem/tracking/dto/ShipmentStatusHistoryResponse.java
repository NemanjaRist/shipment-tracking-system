package com.nemanja.shipmenttrackingsystem.tracking.dto;

import com.nemanja.shipmenttrackingsystem.shipment.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentStatusHistoryResponse(
        Long id,
        ShipmentStatus status,
        LocalDateTime changedAt,
        String note
) {
}