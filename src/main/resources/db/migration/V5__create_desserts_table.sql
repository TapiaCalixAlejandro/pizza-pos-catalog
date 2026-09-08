CREATE TABLE desserts (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    portion BIGINT NOT NULL,
    portion_unit VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_desserts_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE UNIQUE INDEX uk_desserts_product_active
    ON desserts (product_id)
    WHERE deleted_at IS NULL;