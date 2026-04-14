package com.mango.products.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO to respond with the price in effect on a date.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentPriceResponse {

    private BigDecimal value;
    
}
