package com.nemanja.shipmenttrackingsystem.shipment;

import com.nemanja.shipmenttrackingsystem.shipment.dto.UpdateShipmentStatusRequest;
import com.nemanja.shipmenttrackingsystem.tracking.dto.ShipmentStatusHistoryResponse;
import com.nemanja.shipmenttrackingsystem.shipment.dto.CreateShipmentRequest;
import com.nemanja.shipmenttrackingsystem.shipment.dto.ShipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.nemanja.shipmenttrackingsystem.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    public PageResponse<ShipmentResponse> getShipments(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return shipmentService.getShipments(customerId, status, createdFrom, createdTo, pageable);
    }

    @GetMapping("/{id}")
    public ShipmentResponse getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ShipmentResponse getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.getShipmentByTrackingNumber(trackingNumber);
    }

    @GetMapping("/{id}/history")
    public List<ShipmentStatusHistoryResponse> getShipmentStatusHistory(@PathVariable Long id) {
        return shipmentService.getShipmentStatusHistory(id);
    }

    @PatchMapping("/{id}/status")
    public ShipmentResponse updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShipmentStatusRequest request
    ) {
        return shipmentService.updateShipmentStatus(id, request);
    }
}