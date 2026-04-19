package com.mango.products.infrastructure.web.product.contracts;

/**
 * Response contract for a created Product.
 */
public record ProductResponse(
        Long id,
        String name,
        String description) {
}
