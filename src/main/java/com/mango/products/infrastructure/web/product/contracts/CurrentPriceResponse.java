package com.mango.products.infrastructure.web.product.contracts;

import java.math.BigDecimal;

/**
 * Response contract for current price on a specific date.
 */
public record CurrentPriceResponse(
        BigDecimal value) {
}
