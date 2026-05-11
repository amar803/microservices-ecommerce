
CREATE TABLE IF NOT EXISTS report_counters (
    id BIGINT PRIMARY KEY,
    total_orders BIGINT NOT NULL,
    total_payments_captured BIGINT NOT NULL,
    inventory_reservations BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

