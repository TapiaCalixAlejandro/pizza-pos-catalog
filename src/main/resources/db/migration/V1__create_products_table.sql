CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    image VARCHAR(255) NOT NULL,
    product_type VARCHAR(30) NOT NULL,
    product_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT uk_products_name UNIQUE (name)
);