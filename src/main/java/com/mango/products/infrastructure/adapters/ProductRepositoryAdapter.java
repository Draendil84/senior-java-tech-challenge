package com.mango.products.infrastructure.adapters;

import com.mango.products.domain.Product;
import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.ports.ProductRepository;
import com.mango.products.infrastructure.persistence.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the ProductRepository port.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;
    private final PriceMapper priceMapper;

    /**
     * Saves a product to the database.
     * Invalidates caches after saving to maintain consistency.
     *
     * @param product product to save
     * @return the saved product with assigned ID
     */
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product save(Product product) {
        try {
            ProductEntity entity = productMapper.fromDomain(product);

            List<PriceEntity> prices = Optional.ofNullable(product.getPrices())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(priceMapper::fromDomain)
                    .toList();

            prices.forEach(p -> p.setProduct(entity));
            entity.setPrices(prices);

            ProductEntity saved = productJpaRepository.save(entity);

            log.info("Product saved: id={}, name={}, prices={}",
                    saved.getId(), saved.getName(), saved.getPrices().size());

            return productMapper.toDomain(saved);

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new DuplicateProductNameException(product.getName());
            }
            throw e;
        }
    }

    /**
     * Search the product by ID.
     *
     * @param id product identifier
     * @return Optional with the product if available
     */
    @Override
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findById(Long id) {
        log.debug("Searching for product by ID: {}", id);
        return productJpaRepository.findById(id).map(productMapper::toDomain);
    }

    /**
     * Search for a product by name.
     *
     * @param name product name
     * @return Optional with the product if available
     */
    @Override
    @Cacheable(value = "productsByName", key = "#name")
    public Optional<Product> findByName(String name) {
        log.debug("Searching product by name: '{}'", name);
        return productJpaRepository.findByName(name).map(productMapper::toDomain);
    }

    /**
     * Get all the products.
     *
     * @return list of all products.
     */
    @Override
    public List<Product> findAll() {
        log.debug("Obtaining all the products");
        return productJpaRepository.findAll().stream()
                .map(productMapper::toDomain)
                .toList();
    }

    /**
     * Remove the product by ID.
     *
     * @param id product identifier to be deleted
     */
    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteById(Long id) {
        log.info("Removing product: ID={}", id);
        productJpaRepository.deleteById(id);
    }

}
