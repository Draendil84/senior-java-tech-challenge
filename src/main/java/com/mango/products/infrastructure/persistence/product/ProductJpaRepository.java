package com.mango.products.infrastructure.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for database access.
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Searches for a product by name.
     *
     * @param name Product name
     * @return Optional with the product name if it exists
     */
    Optional<ProductEntity> findByName(String name);

}
