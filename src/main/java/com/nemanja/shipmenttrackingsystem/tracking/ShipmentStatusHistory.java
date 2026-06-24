package com.nemanja.shipmenttrackingsystem.tracking;

import com.nemanja.shipmenttrackingsystem.shipment.Shipment;
import com.nemanja.shipmenttrackingsystem.shipment.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipment_status_history")
public class ShipmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(columnDefinition = "TEXT")
    private String note;
}