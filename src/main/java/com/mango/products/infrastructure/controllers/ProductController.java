package com.mango.products.infrastructure.controllers;

import com.mango.products.application.dto.CreatePriceRequest;
import com.mango.products.application.dto.CreateProductRequest;
import com.mango.products.application.dto.ProductResponse;
import com.mango.products.application.dto.ProductWithPricesResponse;
import com.mango.products.application.mappers.ProductDtoMapper;
import com.mango.products.application.usecases.AddPriceUseCase;
import com.mango.products.application.usecases.CreateProductUseCase;
import com.mango.products.application.usecases.GetCurrentPriceUseCase;
import com.mango.products.application.usecases.GetProductPriceHistoryUseCase;
import com.mango.products.domain.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for product and price management.
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final AddPriceUseCase addPriceUseCase;
    private final GetCurrentPriceUseCase getCurrentPriceUseCase;
    private final GetProductPriceHistoryUseCase getProductPriceHistoryUseCase;
    private final ProductDtoMapper productDtoMapper;

    /**
     * Create a new product.
     * POST /products
     *
     * @param request DTO with product name and description
     * @return ProductResponse with the created product
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating product with name: '{}' and description: '{}'", request.getName(), request.getDescription());
        Product product = createProductUseCase.createProduct(request.getName(), request.getDescription());
        ProductResponse response = productDtoMapper.productToResponse(product);
        log.info("Product created successfully with ID: {}", product.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Adds a price to a product.
     * POST /products/{id}/prices
     *
     * @param id      Product ID
     * @param request DTO with price value and date range
     * @return ProductWithPricesResponse with the updated product price
     */
    @PostMapping(value = "/{id}/prices", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductWithPricesResponse> addPrice(
            @PathVariable Long id,
            @Valid @RequestBody CreatePriceRequest request) {
        log.info("Adding price to product ID: {}, value: {}, initDate: {}, endDate: {}",
                id, request.getValue(), request.getInitDate(), request.getEndDate());
        Product product = addPriceUseCase.addPrice(id, request.getValue(), request.getInitDate(), request.getEndDate());
        ProductWithPricesResponse response = productDtoMapper.productToProductWithPricesResponse(product);
        log.info("Price added successfully to product ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the current price of a product on a specific date or the price history of a product.
     *
     * @param id   Product ID
     * @param date Optional date to get a specific price
     * @return CurrentPriceResponse with the current price, or 404 if not found
     * ******* ProductWithPricesResponse with the product and its price history, or 404 if not found
     */
    @GetMapping(value = "/{id}/prices", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPrice(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate date) {
        log.info("Get current price for product ID: {} on date: {}", id, date);
        if (date != null) {
            return getCurrentPriceUseCase.getCurrentPrice(id, date)
                    .map(productDtoMapper::priceToCurrentPriceResponse)
                    .map(response -> ResponseEntity.ok((Object) response))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return getProductPriceHistoryUseCase.getProductPriceHistory(id)
                    .map(productDtoMapper::productToProductWithPricesResponse)
                    .map(response -> ResponseEntity.ok((Object) response))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }

}
