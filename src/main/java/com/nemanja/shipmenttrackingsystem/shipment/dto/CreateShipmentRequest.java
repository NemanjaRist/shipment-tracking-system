package com.nemanja.shipmenttrackingsystem.shipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateShipmentRequest(

        @NotNull(message = "Customer id is required")
        Long customerId,

        @NotBlank(message = "Content description is required")
        @Size(max = 2000, message = "Content description must be at most 2000 characters")
        String contentDescription
) {
}