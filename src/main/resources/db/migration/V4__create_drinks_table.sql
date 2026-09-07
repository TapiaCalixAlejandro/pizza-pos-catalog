CREATE TABLE drinks (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    volume INTEGER NOT NULL,
    is_alcoholic BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_drinks_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE UNIQUE INDEX uk_drinks_product_active
    ON drinks(product_id)
    WHERE deleted_at IS NULL;