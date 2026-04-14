package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.Price;
import com.mango.products.domain.PriceRange;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PriceMapper.
 */
class PriceMapperTest {

    private final PriceMapper mapper = Mappers.getMapper(PriceMapper.class);

    @Test
    void shouldMapEntityToDomain() {
        PriceEntity entity = new PriceEntity();
        entity.setId(1L);
        entity.setValue(BigDecimal.TEN);
        entity.setInitDate(LocalDate.of(2024, 1, 1));
        entity.setEndDate(LocalDate.of(2024, 6, 1));

        Price result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getValue());
        assertNotNull(result.getRange());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void shouldMapDomainToEntity() {
        Price price = new Price();
        price.setId(1L);
        price.setValue(BigDecimal.TEN);
        price.setRange(new PriceRange(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 1)
        ));

        PriceEntity result = mapper.fromDomain(price);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getValue());
    }

    @Test
    void shouldHandleNullRange() {
        Price price = new Price();
        price.setValue(BigDecimal.TEN);
        price.setRange(null);

        PriceEntity result = mapper.fromDomain(price);

        assertNotNull(result);
        assertNull(result.getInitDate());
        assertNull(result.getEndDate());
    }

    @Test
    void shouldReturnNullWhenPriceIsNull() {
        assertNull(mapper.fromDomain(null));
    }

}
