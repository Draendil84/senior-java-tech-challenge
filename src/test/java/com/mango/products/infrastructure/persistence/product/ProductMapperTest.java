package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductMapper.
 */
class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    private static final String NAME = "Zapatillas";
    private static final String DESCRIPTION = "Descripción";

    @Test
    void shouldMapEntityToDomain() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName(NAME);
        entity.setDescription(DESCRIPTION);

        Product result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldMapDomainToEntity() {
        Product product = new Product(NAME, DESCRIPTION);

        ProductEntity result = mapper.fromDomain(product);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    void shouldReturnNullWhenProductIsNull() {
        assertNull(mapper.fromDomain(null));
    }

    @Test
    void shouldIgnorePricesField() {
        Product product = new Product("Zapatillas", "Descripción");

        ProductEntity entity = mapper.fromDomain(product);

        assertNotNull(entity);
        assertNotNull(entity.getPrices());
        assertTrue(entity.getPrices().isEmpty());
    }

    @Test
    void shouldHandleEmptyProductEntity() {
        ProductEntity entity = new ProductEntity();

        Product result = mapper.toDomain(entity);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getDescription());
    }

}
