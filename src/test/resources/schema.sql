-- Schema para tests con H2
-- Crear tabla de productos
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000)
);

-- Crear tabla de precios
CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    price_value NUMERIC(19, 2) NOT NULL,
    init_date DATE NOT NULL,
    end_date DATE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);
