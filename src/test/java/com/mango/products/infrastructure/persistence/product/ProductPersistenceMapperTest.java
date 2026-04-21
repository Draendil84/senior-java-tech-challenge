package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductMapper.
 */
class ProductPersistenceMapperTest {

    private final ProductPersistenceMapper mapper = new ProductPersistenceMapper(new PriceMapper());

    private static final String NAME = "Zapatillas";
    private static final String DESCRIPTION = "Descripción";

    @Test
    void shouldMapEntityToProduct() {
        ProductEntity entity = new ProductEntity(1L, NAME, DESCRIPTION);

        Product result = mapper.toProduct(entity);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toProduct(null));
    }

    @Test
    void shouldMapDomainToEntity() {
        Product product = new Product(NAME, DESCRIPTION);

        ProductEntity result = mapper.toProductEntity(product);

        assertNotNull(result);
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    void shouldReturnNullWhenProductIsNull() {
        assertNull(mapper.toProductEntity(null));
    }

    @Test
    void shouldPreserveProductData() {
        Product product = new Product(1L, NAME, DESCRIPTION);

        ProductEntity entity = mapper.toProductEntity(product);

        assertNotNull(entity);
        assertEquals(NAME, entity.getName());
        assertEquals(DESCRIPTION, entity.getDescription());
        assertNotNull(entity.getPrices());
    }

}
