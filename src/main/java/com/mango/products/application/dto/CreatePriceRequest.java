package com.mango.products.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO to create/update a Price.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePriceRequest {

    @NotNull(message = "The price value cannot be zero")
    @DecimalMin(value = "0.01", message = "The price value must be greater than 0")
    private BigDecimal value;

    @NotNull(message = "The start date cannot be null")
    private LocalDate initDate;

    private LocalDate endDate;
    
}
