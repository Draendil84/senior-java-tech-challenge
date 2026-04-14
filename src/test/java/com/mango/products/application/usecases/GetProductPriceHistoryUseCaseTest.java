package com.mango.products.application.usecases;

import com.mango.products.domain.Product;
import com.mango.products.domain.ports.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GetProductPriceHistoryUseCase.
 */
@ExtendWith(MockitoExtension.class)
class GetProductPriceHistoryUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductPriceHistoryUseCase getProductPriceHistoryUseCase;

    @Test
    void testGetProductPriceHistorySuccessfully() {
        Product product = new Product(1L, "Zapatillas", "Descripción");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = getProductPriceHistoryUseCase.getProductPriceHistory(1L);

        assertTrue(result.isPresent());
        assertEquals("Zapatillas", result.get().getName());
    }

    @Test
    void testGetProductPriceHistoryNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = getProductPriceHistoryUseCase.getProductPriceHistory(999L);

        assertFalse(result.isPresent());
    }
    
}
