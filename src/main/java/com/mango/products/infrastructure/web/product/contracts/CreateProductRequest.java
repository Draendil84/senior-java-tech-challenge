package com.mango.products.infrastructure.web.product.contracts;

/**
 * Request contract for creating a Product.
 */
public record CreateProductRequest(
        String name,
        String description) {
}
