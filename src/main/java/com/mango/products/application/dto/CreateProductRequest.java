package com.mango.products.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to create a Product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "The product name is required")
    @Size(min = 1, max = 255, message = "The name must be between 1 and 255 characters long")
    private String name;

    @Size(max = 1000, message = "The description cannot exceed 1000 characters")
    private String description;
    
}
