package com.mango.products.application.usecases;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GetPriceUseCase.
 */
@ExtendWith(MockitoExtension.class)
class GetPriceUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private GetPriceUseCase getPriceUseCase;

    private static final Long PRODUCT_ID = 1L;
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 4, 15);

    @Test
    void shouldReturnCurrentPriceWhenDateIsProvided() {
        Product product = new Product(PRODUCT_ID, "Test Product", "Test Description");
        Price expectedPrice = new Price(BigDecimal.valueOf(99.99), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        product.addPrice(expectedPrice);

        when(productRepositoryPort.findById(PRODUCT_ID))
                .thenReturn(Optional.of(product));

        var priceInfo = getPriceUseCase.getPrice(PRODUCT_ID, TEST_DATE);

        assertTrue(priceInfo.hasData());
        assertTrue(priceInfo.getCurrentPrice().isPresent());
        assertFalse(priceInfo.getProductWithHistory().isPresent());
        assertEquals(expectedPrice.getValue(), priceInfo.getCurrentPrice().get().getValue());
        verify(productRepositoryPort).findById(PRODUCT_ID);
    }

    @Test
    void shouldReturnPriceHistoryWhenDateIsNull() {
        Product expectedProduct = new Product(PRODUCT_ID, "Test Product", "Test Description");
        when(productRepositoryPort.findById(PRODUCT_ID))
                .thenReturn(Optional.of(expectedProduct));

        var priceInfo = getPriceUseCase.getPrice(PRODUCT_ID, null);

        assertTrue(priceInfo.hasData());
        assertFalse(priceInfo.getCurrentPrice().isPresent());
        assertTrue(priceInfo.getProductWithHistory().isPresent());
        assertEquals(expectedProduct.getId(), priceInfo.getProductWithHistory().get().getId());
        verify(productRepositoryPort).findById(PRODUCT_ID);
    }

    @Test
    void shouldReturnEmptyPriceInfoWhenCurrentPriceNotFound() {
        Product product = new Product(PRODUCT_ID, "Test Product", "Test Description");
        when(productRepositoryPort.findById(PRODUCT_ID))
                .thenReturn(Optional.of(product)); // Product exists but no price for this date

        var priceInfo = getPriceUseCase.getPrice(PRODUCT_ID, TEST_DATE);

        assertFalse(priceInfo.hasData());
        assertFalse(priceInfo.getCurrentPrice().isPresent());
        assertFalse(priceInfo.getProductWithHistory().isPresent());
    }

    @Test
    void shouldReturnEmptyPriceInfoWhenProductNotFound() {
        when(productRepositoryPort.findById(PRODUCT_ID))
                .thenReturn(Optional.empty());

        var priceInfo = getPriceUseCase.getPrice(PRODUCT_ID, null);

        assertFalse(priceInfo.hasData());
        assertFalse(priceInfo.getCurrentPrice().isPresent());
        assertFalse(priceInfo.getProductWithHistory().isPresent());
    }
}

