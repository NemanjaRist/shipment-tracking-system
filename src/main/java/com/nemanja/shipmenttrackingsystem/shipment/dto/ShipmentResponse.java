package com.nemanja.shipmenttrackingsystem.shipment.dto;

import com.nemanja.shipmenttrackingsystem.shipment.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentResponse(
        Long id,
        String trackingNumber,
        String contentDescription,
        ShipmentStatus status,
        ShipmentCustomerResponse customer,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}