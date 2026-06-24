package com.nemanja.shipmenttrackingsystem.shipment.dto;

import com.nemanja.shipmenttrackingsystem.shipment.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateShipmentStatusRequest(

        @NotNull(message = "Status is required")
        ShipmentStatus status,

        @Size(max = 2000, message = "Note must be at most 2000 characters")
        String note
) {
}