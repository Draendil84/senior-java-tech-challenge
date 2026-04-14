package com.mango.products.domain.ports;

import com.mango.products.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Interface that defines persistence operations for products.
 */
public interface ProductRepository {

    /**
     * Save a product to the repository.
     *
     * @param product product to save
     * @return product saved with assigned ID
     */
    Product save(Product product);

    /**
     * Remove a product by its ID.
     *
     * @param id product identifier to be deleted
     */
    void deleteById(Long id);

    /**
     * Get a product by ID.
     *
     * @param id product identifier
     * @return Optional with the product if available
     */
    Optional<Product> findById(Long id);

    /**
     * Search for a product by name.
     *
     * @param name product name
     * @return Optional with the product if available
     */
    Optional<Product> findByName(String name);

    /**
     * Get all the products.
     *
     * @return list with all products
     */
    List<Product> findAll();
    
}
