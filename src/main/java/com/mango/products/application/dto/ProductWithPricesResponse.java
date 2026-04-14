package com.mango.products.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO to respond with the complete price history of a product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithPricesResponse {

    private Long id;
    private String name;
    private String description;
    private List<PriceResponse> prices;
    
}
