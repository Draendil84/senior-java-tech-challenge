package com.mango.products.infrastructure.web.product.mappers;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.web.product.contracts.CurrentPriceResponse;
import com.mango.products.infrastructure.web.product.contracts.PriceResponse;
import com.mango.products.infrastructure.web.product.contracts.ProductResponse;
import com.mango.products.infrastructure.web.product.contracts.ProductWithPricesResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductContractMapperTest {

    @Test
    void toPriceResponse_shouldMapAllFields() {
        Price price = new Price(1L, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));

        PriceResponse response = ProductContractMapper.toPriceResponse(price);

        assertEquals(BigDecimal.valueOf(100), response.value());
        assertEquals(LocalDate.of(2024, 1, 1), response.initDate());
        assertEquals(LocalDate.of(2024, 12, 31), response.endDate());
    }

    @Test
    void toPriceResponse_shouldHandleNullEndDate() {
        Price price = new Price(1L, BigDecimal.TEN, LocalDate.of(2024, 1, 1),null);

        PriceResponse response = ProductContractMapper.toPriceResponse(price);

        assertEquals(BigDecimal.TEN, response.value());
        assertEquals(LocalDate.of(2024, 1, 1), response.initDate());
        assertNull(response.endDate());
    }

    @Test
    void toProductResponse_shouldMapCorrectly() {
        Product product = new Product(10L, "Laptop", "Gaming laptop");

        ProductResponse response = ProductContractMapper.toProductResponse(product);

        assertEquals(10L, response.id());
        assertEquals("Laptop", response.name());
        assertEquals("Gaming laptop", response.description());
    }

    @Test
    void toProductWithPricesResponse_shouldMapWithSinglePrice() {
        Product product = new Product(1L,"Phone", "Smartphone");

        Price price = new Price(2L, BigDecimal.valueOf(500), LocalDate.now(), null);

        product.addPrice(price);

        ProductWithPricesResponse response = ProductContractMapper.toProductWithPricesResponse(product);

        assertEquals(1L, response.id());
        assertEquals("Phone", response.name());
        assertEquals("Smartphone", response.description());

        assertEquals(1, response.prices().size());
        assertEquals(BigDecimal.valueOf(500), response.prices().getFirst().value());
    }

    @Test
    void toProductWithPricesResponse_shouldHandleEmptyPrices() {
        Product product = new Product(1L, "Empty", "No prices");

        ProductWithPricesResponse response = ProductContractMapper.toProductWithPricesResponse(product);

        assertNotNull(response.prices());
        assertTrue(response.prices().isEmpty());
    }

    @Test
    void toProductWithPricesResponse_shouldMapMultiplePrices() {
        Product product = new Product(99L, "Multi", "Multiple prices");

        product.addPrice(
                new Price(BigDecimal.TEN, LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 1)));
        product.addPrice(
                new Price(BigDecimal.TWO, LocalDate.of(2024, 6, 2),
                        LocalDate.of(2024, 12, 31)));

        ProductWithPricesResponse response = ProductContractMapper.toProductWithPricesResponse(product);

        assertEquals(2, response.prices().size());
        assertEquals(BigDecimal.TEN, response.prices().get(0).value());
        assertEquals(BigDecimal.TWO, response.prices().get(1).value());
    }

    @Test
    void toCurrentPriceResponse_shouldMapCorrectly() {
        Price price = new Price(5L, BigDecimal.valueOf(199.99), LocalDate.now(), null);

        CurrentPriceResponse response = ProductContractMapper.toCurrentPriceResponse(price);

        assertEquals(BigDecimal.valueOf(199.99), response.value());
    }

    @Test
    void stream_mapping_shouldExecuteLambdaBranch() {
        Product product = new Product(1L, "StreamTest", "Testing stream branch");

        product.addPrice(new Price(1L, BigDecimal.valueOf(123), LocalDate.now(), null));

        ProductWithPricesResponse response = ProductContractMapper.toProductWithPricesResponse(product);

        assertEquals(1, response.prices().size());
    }

}
