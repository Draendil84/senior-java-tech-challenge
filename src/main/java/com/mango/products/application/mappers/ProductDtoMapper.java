package com.mango.products.application.mappers;

import com.mango.products.application.dto.CurrentPriceResponse;
import com.mango.products.application.dto.PriceResponse;
import com.mango.products.application.dto.ProductResponse;
import com.mango.products.application.dto.ProductWithPricesResponse;
import com.mango.products.domain.Price;
import com.mango.products.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Centralized mapper for converting domain entities to DTOs.
 * Responsible for the transformation between the domain layer and the presentation layer.
 */
@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    PriceResponse priceToResponse(Price price);

    ProductResponse productToResponse(Product product);

    @Mapping(target = "prices", source = "prices")
    ProductWithPricesResponse productToProductWithPricesResponse(Product product);

    CurrentPriceResponse priceToCurrentPriceResponse(Price price);

}
