package com.mango.products.infrastructure.web.product.mappers;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.web.product.contracts.CurrentPriceResponse;
import com.mango.products.infrastructure.web.product.contracts.PriceResponse;
import com.mango.products.infrastructure.web.product.contracts.ProductResponse;
import com.mango.products.infrastructure.web.product.contracts.ProductWithPricesResponse;

import java.util.List;

/**
 * Converter from domain entities to DTOs (Presentation Layer).
 */
public class ProductContractMapper {

    private ProductContractMapper() {
    }

    /**
     * Converts a Price domain entity to a PriceResponse DTO.
     */
    public static PriceResponse toPriceResponse(Price price) {
        return new PriceResponse(
                price.getValue(),
                price.getInitDate(),
                price.getEndDate()
        );
    }

    /**
     * Converts a Product domain entity to a ProductResponse DTO.
     */
    public static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription()
        );
    }

    /**
     * Converts a Product domain entity to a ProductWithPricesResponse DTO.
     * Includes the complete price history.
     */
    public static ProductWithPricesResponse toProductWithPricesResponse(Product product) {
        List<PriceResponse> prices = product.getPrices().stream()
                .map(ProductContractMapper::toPriceResponse)
                .toList();

        return new ProductWithPricesResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                prices
        );
    }

    /**
     * Converts a Price domain entity to a CurrentPriceResponse DTO.
     */
    public static CurrentPriceResponse toCurrentPriceResponse(Price price) {
        return new CurrentPriceResponse(price.getValue());
    }
}
