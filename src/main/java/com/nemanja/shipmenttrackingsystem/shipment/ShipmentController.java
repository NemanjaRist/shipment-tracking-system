package com.nemanja.shipmenttrackingsystem.shipment;

import com.nemanja.shipmenttrackingsystem.shipment.dto.CreateShipmentRequest;
import com.nemanja.shipmenttrackingsystem.shipment.dto.ShipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponse createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        return shipmentService.createShipment(request);
    }

    @GetMapping
    public List<ShipmentResponse> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public ShipmentResponse getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ShipmentResponse getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.getShipmentByTrackingNumber(trackingNumber);
    }
}