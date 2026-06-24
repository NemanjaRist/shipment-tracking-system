package com.nemanja.shipmenttrackingsystem.shipment;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShipmentSpecification {

    private ShipmentSpecification() {
    }

    public static Specification<Shipment> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> {
            if (customerId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("customer").get("id"), customerId);
        };
    }

    public static Specification<Shipment> hasStatus(ShipmentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Shipment> createdFrom(LocalDate createdFrom) {
        return (root, query, criteriaBuilder) -> {
            if (createdFrom == null) {
                return criteriaBuilder.conjunction();
            }

            LocalDateTime fromDateTime = createdFrom.atStartOfDay();

            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDateTime);
        };
    }

    public static Specification<Shipment> createdTo(LocalDate createdTo) {
        return (root, query, criteriaBuilder) -> {
            if (createdTo == null) {
                return criteriaBuilder.conjunction();
            }

            LocalDateTime toDateTimeExclusive = createdTo.plusDays(1).atStartOfDay();

            return criteriaBuilder.lessThan(root.get("createdAt"), toDateTimeExclusive);
        };
    }
}