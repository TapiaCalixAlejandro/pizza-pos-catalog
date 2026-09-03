CREATE TABLE pizzas (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    preparation_time INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT uk_pizzas_product UNIQUE (product_id),
    CONSTRAINT fk_pizzas_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE pizza_ingredients (
    id BIGSERIAL PRIMARY KEY,
    pizza_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity NUMERIC(10, 3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_pizza_ingredients_pizza FOREIGN KEY (pizza_id) REFERENCES pizzas (id),
    CONSTRAINT fk_pizza_ingredients_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients (id),
    CONSTRAINT chk_pizza_ingredients_quantity CHECK (quantity > 0)
);

CREATE UNIQUE INDEX uk_pizza_ingredients_active
    ON pizza_ingredients (pizza_id, ingredient_id)
    WHERE deleted_at IS NULL;