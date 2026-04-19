package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JdbcProductRepository jdbcProductRepository;
    private final ProductPersistenceMapper productPersistenceMapper;
    private final PriceMapper priceMapper;

    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryAdapter.class);

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
            ProductEntity entity = productPersistenceMapper.fromDomain(product);

            List<PriceEntity> prices = Optional.ofNullable(product.getPrices())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(p -> priceMapper.fromDomain(p, entity))
                    .toList();

            entity.setPrices(prices);

            ProductEntity saved = jdbcProductRepository.save(entity);

            log.info("Product saved: id={}, name={}, prices={}",
                    saved.getId(), saved.getName(), saved.getPrices().size());

            return productPersistenceMapper.toDomain(saved);

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateProductNameException(product.getName());
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
        return jdbcProductRepository.findById(id).map(productPersistenceMapper::toDomain);
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
        return jdbcProductRepository.findByName(name).map(productPersistenceMapper::toDomain);
    }

    /**
     * Get all the products.
     *
     * @return list of all products.
     */
    @Override
    public List<Product> findAll() {
        log.debug("Obtaining all the products");
        return jdbcProductRepository.findAll().stream()
                .map(productPersistenceMapper::toDomain)
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
        jdbcProductRepository.deleteById(id);
    }
}
