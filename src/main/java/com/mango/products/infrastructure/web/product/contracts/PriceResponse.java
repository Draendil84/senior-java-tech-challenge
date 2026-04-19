package com.mango.products.infrastructure.web.product.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response contract for price information.
 */
public record PriceResponse(
        BigDecimal value,
        LocalDate initDate,
        LocalDate endDate) {
}
