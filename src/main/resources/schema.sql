-- Schema para H2 Database
-- Crear tabla de productos
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000)
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Crear tabla de precios
CREATE TABLE IF NOT EXISTS prices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    price_value NUMERIC(19, 2) NOT NULL,
    init_date DATE NOT NULL,
    end_date DATE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_prices_product_id ON prices(product_id);
