package com.mango.products.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO to respond with price information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {

    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;
    
}
