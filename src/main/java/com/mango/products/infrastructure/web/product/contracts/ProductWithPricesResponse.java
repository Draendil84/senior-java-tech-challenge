package com.mango.products.infrastructure.web.product.contracts;

import java.util.List;

/**
 * Response contract for a Product with its complete price history.
 */
public record ProductWithPricesResponse(
        Long id,
        String name,
        String description,
        List<PriceResponse> prices) {
}
