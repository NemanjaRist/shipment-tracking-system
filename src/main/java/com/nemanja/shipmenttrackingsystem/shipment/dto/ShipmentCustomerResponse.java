package com.nemanja.shipmenttrackingsystem.shipment.dto;

public record ShipmentCustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}