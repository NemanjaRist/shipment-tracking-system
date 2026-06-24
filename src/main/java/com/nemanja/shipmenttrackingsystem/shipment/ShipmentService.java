package com.nemanja.shipmenttrackingsystem.shipment;

import com.nemanja.shipmenttrackingsystem.common.exception.ResourceNotFoundException;
import com.nemanja.shipmenttrackingsystem.customer.Customer;
import com.nemanja.shipmenttrackingsystem.customer.CustomerService;
import com.nemanja.shipmenttrackingsystem.shipment.dto.CreateShipmentRequest;
import com.nemanja.shipmenttrackingsystem.shipment.dto.ShipmentCustomerResponse;
import com.nemanja.shipmenttrackingsystem.shipment.dto.ShipmentResponse;
import com.nemanja.shipmenttrackingsystem.tracking.ShipmentStatusHistory;
import com.nemanja.shipmenttrackingsystem.tracking.ShipmentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository statusHistoryRepository;
    private final CustomerService customerService;

    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        Customer customer = customerService.findCustomerById(request.customerId());

        Shipment shipment = Shipment.builder()
                .trackingNumber(generateTrackingNumber())
                .contentDescription(request.contentDescription())
                .status(ShipmentStatus.CREATED)
                .customer(customer)
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(savedShipment)
                .status(ShipmentStatus.CREATED)
                .changedAt(LocalDateTime.now())
                .note("Shipment created")
                .build();

        statusHistoryRepository.save(history);

        return mapToResponse(savedShipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(Long id) {
        Shipment shipment = findShipmentById(id);
        return mapToResponse(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with tracking number: " + trackingNumber
                ));

        return mapToResponse(shipment);
    }

    @Transactional(readOnly = true)
    public Shipment findShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
    }

    private String generateTrackingNumber() {
        String trackingNumber;

        do {
            trackingNumber = "TRK-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        } while (shipmentRepository.existsByTrackingNumber(trackingNumber));

        return trackingNumber;
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        Customer customer = shipment.getCustomer();

        ShipmentCustomerResponse customerResponse = new ShipmentCustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );

        return new ShipmentResponse(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getContentDescription(),
                shipment.getStatus(),
                customerResponse,
                shipment.getCreatedAt(),
                shipment.getUpdatedAt()
        );
    }
}