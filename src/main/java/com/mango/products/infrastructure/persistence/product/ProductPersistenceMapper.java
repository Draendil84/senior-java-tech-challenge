package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converter between ProductEntity (Persistence) and Product (Domain).
 */
@Component
public class ProductPersistenceMapper {

    private final PriceMapper priceMapper;

    public ProductPersistenceMapper(PriceMapper priceMapper) {
        this.priceMapper = priceMapper;
    }

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity JPA entity retrieved from database
     * @return domain product
     */
    public Product toProduct(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        Product product = new Product(entity.getId(), entity.getName(), entity.getDescription());
        if (entity.getPrices() != null) {
            List<Price> prices = entity.getPrices().stream()
                    .map(priceMapper::toPrice)
                    .toList();
            prices.forEach(product::addPrice);
        }
        return product;
    }

    /**
     * Converts a domain entity to a persistence entity.
     *
     * @param product domain product
     * @return persistence entity ready to persist
     */
    public ProductEntity toProductEntity(Product product) {
        if (product == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity(product.getId(), product.getName(), product.getDescription());
        if (product.getPrices() != null && !product.getPrices().isEmpty()) {
            List<PriceEntity> priceEntities = product.getPrices().stream()
                    .map(price -> priceMapper.toPriceEntity(price, entity))
                    .toList();
            entity.setPrices(priceEntities);
        }
        return entity;
    }
}
