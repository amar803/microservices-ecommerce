CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(64) NOT NULL,
    items_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

