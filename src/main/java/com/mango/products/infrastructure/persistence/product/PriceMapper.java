package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.Price;
import com.mango.products.domain.PriceRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

/**
 * Mapper to convert between PriceEntity (JPA) and Price (Domain).
 */
@Mapper(componentModel = "spring")
public abstract class PriceMapper {

    @Mapping(target = "range", expression = "java(mapToRange(entity))")
    public abstract Price toDomain(PriceEntity entity);

    @Mapping(target = "initDate", expression = "java(mapInitDate(price))")
    @Mapping(target = "endDate", expression = "java(mapEndDate(price))")
    @Mapping(target = "product", ignore = true)
    public abstract PriceEntity fromDomain(Price price);

    protected PriceRange mapToRange(PriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PriceRange(entity.getInitDate(), entity.getEndDate());
    }

    protected LocalDate mapInitDate(Price price) {
        return price != null && price.getRange() != null
                ? price.getRange().getInitDate()
                : null;
    }

    protected LocalDate mapEndDate(Price price) {
        return price != null && price.getRange() != null
                ? price.getRange().getEndDate()
                : null;
    }

}
