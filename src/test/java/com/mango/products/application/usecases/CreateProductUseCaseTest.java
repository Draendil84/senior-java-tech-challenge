package com.mango.products.application.usecases;

import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.exceptions.InvalidProductException;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CreateProductUseCase.
 */
@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    void testCreateProductSuccessfully() {
        Product mockProduct = new Product(1L, "Zapatillas deportivas", "Descripción");
        when(productRepositoryPort.findByName("Zapatillas deportivas")).thenReturn(Optional.empty());
        when(productRepositoryPort.save(any(Product.class))).thenReturn(mockProduct);

        Product result = createProductUseCase.createProduct("Zapatillas deportivas", "Descripción");

        assertNotNull(result);
        assertEquals("Zapatillas deportivas", result.getName());
        assertEquals("Descripción", result.getDescription());
    }

    @Test
    void testCreateProductWithEmptyNameThrowsException() {
        assertThrows(InvalidProductException.class, () ->
                createProductUseCase.createProduct("", "Descripción"));
    }

    @Test
    void testCreateProductWithNullNameThrowsException() {
        assertThrows(InvalidProductException.class, () ->
                createProductUseCase.createProduct(null, "Descripción"));
    }

    @Test
    void testCreateProductWithBlankNameThrowsException() {
        assertThrows(InvalidProductException.class, () ->
                createProductUseCase.createProduct("   ", "Descripción"));
    }

    @Test
    void testCreateProductWithDuplicateNameThrowsException() {
        // Simular que el producto ya existe en el repositorio
        Product existingProduct = new Product(1L, "Zapatillas deportivas", "Descripción existente");

        when(productRepositoryPort.findByName("Zapatillas deportivas"))
                .thenReturn(Optional.of(existingProduct));

        assertThrows(DuplicateProductNameException.class, () ->
                createProductUseCase.createProduct("Zapatillas deportivas", "Nueva descripción"));
    }

}
