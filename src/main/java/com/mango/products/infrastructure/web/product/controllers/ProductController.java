package com.mango.products.infrastructure.web.product.controllers;

import com.mango.products.application.usecases.AddPriceUseCase;
import com.mango.products.application.usecases.CreateProductUseCase;
import com.mango.products.application.usecases.GetPriceUseCase;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.web.product.contracts.CreatePriceRequest;
import com.mango.products.infrastructure.web.product.contracts.CreateProductRequest;
import com.mango.products.infrastructure.web.product.contracts.ProductResponse;
import com.mango.products.infrastructure.web.product.contracts.ProductWithPricesResponse;
import com.mango.products.infrastructure.web.product.mappers.ProductContractMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for product and price management.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final AddPriceUseCase addPriceUseCase;
    private final GetPriceUseCase getPriceUseCase;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    /**
     * Create a new product.
     *
     * @param request DTO with product name and description
     * @return ProductResponse with the created product
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "INVALID_PRODUCT: Product validation failed (empty name, too long, etc.)",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "DUPLICATE_PRODUCT_NAME: Product name already exists",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "INTERNAL_ERROR: Unexpected server error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        log.info("Creating product with name: '{}' and description: '{}'", request.name(), request.description());
        Product product = createProductUseCase.createProduct(request.name(), request.description());
        ProductResponse response = ProductContractMapper.toProductResponse(product);
        log.info("Product created successfully with ID: {}", product.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Adds a price to a product.
     *
     * @param id      Product ID
     * @param request DTO with price value and date range
     * @return ProductWithPricesResponse with the updated product price
     */
    @PostMapping(value = "/{id}/prices", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price added successfully"),
            @ApiResponse(responseCode = "400", description = "INVALID_PRICE: Price validation failed (value <= 0, invalid dates, etc.)",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "PRODUCT_NOT_FOUND: Product with the specified ID does not exist",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "PRICE_OVERLAP: Price date range overlaps with existing prices for this product",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "INTERNAL_ERROR: Unexpected server error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ProductWithPricesResponse> addPrice(
            @PathVariable Long id,
            @RequestBody CreatePriceRequest request) {
        log.info("Adding price to product ID: {}, value: {}, initDate: {}, endDate: {}",
                id, request.value(), request.initDate(), request.endDate());
        Product product = addPriceUseCase.addPrice(id, request.value(), request.initDate(), request.endDate());
        ProductWithPricesResponse response = ProductContractMapper.toProductWithPricesResponse(product);
        log.info("Price added successfully to product ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the current price of a product on a specific date or the price history of a product.
     * <p>
     * When date parameter is provided: Returns the price for that specific date
     * When date parameter is NOT provided: Returns the complete price history
     *
     * @param id   Product ID
     * @param date Optional date to get a specific price
     * @return CurrentPriceResponse with the current price (if date provided),
     * or ProductWithPricesResponse with the product and its price history (if date not provided)
     */
    @GetMapping(value = "/{id}/prices", produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "PRODUCT_NOT_FOUND: Product not found or no price for the specified date",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "INTERNAL_ERROR: Unexpected server error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Object> getPrice(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate date) {
        log.info("Get price information for product ID: {} on date: {}", id, date);
        var priceInfo = getPriceUseCase.getPrice(id, date);

        if (!priceInfo.hasData()) {
            log.warn("No price information found for product ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        return priceInfo.getCurrentPrice()
                .map(price -> ResponseEntity.ok(
                        (Object) ProductContractMapper.toCurrentPriceResponse(price)
                ))
                .orElseGet(() -> ResponseEntity.ok(
                        ProductContractMapper.toProductWithPricesResponse(
                                priceInfo.getProductWithHistory().orElseThrow()
                        )
                ));
    }

}
