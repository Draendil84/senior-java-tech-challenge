package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PriceMapper.
 */
class PriceMapperTest {

    private final PriceMapper mapper = new PriceMapper();

    @Test
    void shouldMapEntityToPrice() {
        PriceEntity entity = new PriceEntity(1L, BigDecimal.TEN, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1));

        Price result = mapper.toPrice(entity);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getValue());
        assertNotNull(result.getRange());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toPrice(null));
    }

    @Test
    void shouldMapDomainToEntity() {
        Price price = new Price(1L, BigDecimal.TEN, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1));

        PriceEntity result = mapper.toPriceEntity(price);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getValue());
    }

    @Test
    void shouldMapDomainToEntityWithProduct() {
        Price price = new Price(1L, BigDecimal.TEN, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1));
        ProductEntity product = new ProductEntity(1L, "Test", "Description");

        PriceEntity result = mapper.toPriceEntity(price, product);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getValue());
        assertEquals(product, result.getProduct());
    }

    @Test
    void shouldReturnNullWhenPriceIsNull() {
        assertNull(mapper.toPriceEntity(null));
    }

}
