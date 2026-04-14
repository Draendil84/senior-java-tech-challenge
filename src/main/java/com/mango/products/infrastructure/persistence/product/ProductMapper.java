package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper to convert between ProductEntity (JPA) and Product (Domain).
 */
@Mapper(componentModel = "spring", uses = PriceMapper.class)
public interface ProductMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity JPA entity retrieved from database
     * @return domain product
     */
    Product toDomain(ProductEntity entity);

    /**
     * Converts a domain entity to a JPA entity.
     *
     * @param product domain product
     * @return JPA entity ready to persist
     */
    @Mapping(target = "prices", ignore = true)
    ProductEntity fromDomain(Product product);

}
