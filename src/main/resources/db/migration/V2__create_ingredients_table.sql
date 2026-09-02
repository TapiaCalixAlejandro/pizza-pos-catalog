CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    unit VARCHAR(30) NOT NULL,
    stock NUMERIC(10, 2) NOT NULL,
    minimum_stock NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    cost NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

CREATE UNIQUE INDEX uk_ingredients_name_active
    ON ingredients (name)
    WHERE deleted_at IS NULL;