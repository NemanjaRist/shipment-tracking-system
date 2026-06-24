CREATE TABLE shipments
(
    id                  BIGSERIAL PRIMARY KEY,
    tracking_number     VARCHAR(50) NOT NULL UNIQUE,
    content_description TEXT        NOT NULL,
    status              VARCHAR(30) NOT NULL,
    customer_id         BIGINT      NOT NULL,
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP   NOT NULL,

    CONSTRAINT fk_shipments_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers (id)
);

CREATE INDEX idx_shipments_customer_id ON shipments (customer_id);
CREATE INDEX idx_shipments_status ON shipments (status);
CREATE INDEX idx_shipments_created_at ON shipments (created_at);