package com.mango.products.infrastructure.web.product.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request contract for creating/updating a Price for a Product.
 */
public record CreatePriceRequest(
        BigDecimal value,
        LocalDate initDate,
        LocalDate endDate) {
}
