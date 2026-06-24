CREATE TABLE shipment_status_history
(
    id          BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT      NOT NULL,
    status      VARCHAR(30) NOT NULL,
    changed_at  TIMESTAMP   NOT NULL,
    note        TEXT,

    CONSTRAINT fk_status_history_shipment
        FOREIGN KEY (shipment_id)
            REFERENCES shipments (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_status_history_shipment_id ON shipment_status_history (shipment_id);
CREATE INDEX idx_status_history_changed_at ON shipment_status_history (changed_at);