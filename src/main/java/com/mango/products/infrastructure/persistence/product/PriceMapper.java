package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.model.Price;
import org.springframework.stereotype.Component;

/**
 * Converter between PriceEntity (Persistence) and Price (Domain).
 */
@Component
public class PriceMapper {

    /**
     * Converts a persistence entity to a domain entity.
     *
     * @param entity persistence entity from database
     * @return domain price
     */
    public Price toPrice(PriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Price(entity.getId(), entity.getValue(), entity.getInitDate(), entity.getEndDate());
    }

    /**
     * Converts a domain entity to a persistence entity.
     *
     * @param price   domain price
     * @param product the associated product entity
     * @return persistence entity ready to persist
     */
    public PriceEntity toPriceEntity(Price price, ProductEntity product) {
        if (price == null) {
            return null;
        }
        PriceEntity entity = new PriceEntity(
                price.getId(),
                price.getValue(),
                price.getInitDate(),
                price.getEndDate()
        );
        entity.setProduct(product);
        return entity;
    }

    /**
     * Converts a domain entity to a persistence entity without product association.
     *
     * @param price domain price
     * @return persistence entity ready to persist
     */
    public PriceEntity toPriceEntity(Price price) {
        if (price == null) {
            return null;
        }
        return new PriceEntity(
                price.getId(),
                price.getValue(),
                price.getInitDate(),
                price.getEndDate()
        );
    }

}
