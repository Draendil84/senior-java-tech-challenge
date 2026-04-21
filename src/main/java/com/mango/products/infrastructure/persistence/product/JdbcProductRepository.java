package com.mango.products.infrastructure.persistence.product;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JDBC-based repository for Product persistence.
 */
@Repository
@RequiredArgsConstructor
@Transactional
public class JdbcProductRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcProductRepository.class);

    private static final String INSERT_PRODUCT = "INSERT INTO products (name, description) VALUES (?, ?)";

    private static final String INSERT_PRICE =
            "INSERT INTO prices (product_id, price_value, init_date, end_date) VALUES (?, ?, ?, ?)";

    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM products WHERE id = ?";

    private static final String SELECT_PRODUCT_BY_NAME = "SELECT * FROM products WHERE name = ?";

    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM products";

    private static final String SELECT_PRICES_BY_PRODUCT =
            "SELECT * FROM prices WHERE product_id = ? ORDER BY init_date ASC";

    private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";

    private static final String DELETE_PRICES_BY_PRODUCT = "DELETE FROM prices WHERE product_id = ?";

    private static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, description = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PriceEntity> priceRowMapper = (rs, rowNum) -> mapPriceRow(rs);

    /**
     * Saves a product with its prices to the database.
     * If the product has an ID, updates it; otherwise creates a new one.
     *
     * @param product the product entity to save
     * @return the saved product with assigned ID
     * @throws org.springframework.dao.DataIntegrityViolationException if name is not unique
     */
    public ProductEntity save(ProductEntity product) {
        if (product.getId() != null) {
            updateProduct(product);
        } else {
            insertProduct(product);
        }

        return product;
    }

    /**
     * Insert a new product.
     */
    private void insertProduct(ProductEntity product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            return ps;
        }, keyHolder);

        Long productId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        product.setId(productId);

        if (product.getPrices() != null && !product.getPrices().isEmpty()) {
            for (PriceEntity price : product.getPrices()) {
                price.setProduct(product);
                insertPrice(productId, price);
            }
        }

        log.info("Product inserted: id={}, name={}, prices={}",
                productId, product.getName(),
                product.getPrices() != null ? product.getPrices().size() : 0);
    }

    /**
     * Update an existing product.
     */
    private void updateProduct(ProductEntity product) {
        jdbcTemplate.update(UPDATE_PRODUCT, product.getName(), product.getDescription(), product.getId());

        if (product.getPrices() != null) {
            for (PriceEntity price : product.getPrices()) {
                if (price.getId() == null) {
                    insertPrice(product.getId(), price);
                }
            }
        }

        log.info("Product updated: id={}, name={}", product.getId(), product.getName());
    }

    /**
     * Insert a single price for a product.
     */
    private void insertPrice(Long productId, PriceEntity price) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_PRICE, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, productId);
            ps.setBigDecimal(2, price.getValue());
            ps.setDate(3, Date.valueOf(price.getInitDate()));
            ps.setDate(4, price.getEndDate() != null ? Date.valueOf(price.getEndDate()) : null);
            return ps;
        }, keyHolder);

        Long priceId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        price.setId(priceId);
    }

    /**
     * Finds a product by ID with its associated prices.
     *
     * @param id the product ID
     * @return Optional containing the product if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductEntity> findById(Long id) {
        log.debug("Searching for product by ID: {}", id);

        return jdbcTemplate.query(
                        SELECT_PRODUCT_BY_ID,
                        (rs, rowNum) -> mapProductRow(rs),
                        id
                )
                .stream()
                .findFirst()
                .map(this::enrichProduct);
    }

    /**
     * Finds a product by name with its associated prices.
     *
     * @param name the product name
     * @return Optional containing the product if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductEntity> findByName(String name) {
        log.debug("Searching product by name: '{}'", name);

        return jdbcTemplate.query(
                        SELECT_PRODUCT_BY_NAME,
                        (rs, rowNum) -> mapProductRow(rs),
                        name
                )
                .stream()
                .findFirst()
                .map(this::enrichProduct);
    }

    /**
     * Retrieves all products with their associated prices.
     *
     * @return list of all products
     */
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        log.debug("Obtaining all the products");

        List<ProductEntity> products = jdbcTemplate.query(
                SELECT_ALL_PRODUCTS,
                (rs, rowNum) -> mapProductRow(rs)
        );

        products.forEach(product -> {
            List<PriceEntity> prices = loadPricesByProductId(product.getId());
            product.setPrices(prices);
        });

        return products;
    }

    /**
     * Deletes a product and its associated prices by ID.
     *
     * @param id the product ID
     */
    public void deleteById(Long id) {
        log.info("Removing product: ID={}", id);

        jdbcTemplate.update(DELETE_PRICES_BY_PRODUCT, id);

        jdbcTemplate.update(DELETE_PRODUCT, id);
    }

    /**
     * Enriches a product entity by loading its associated prices.
     *
     * @param product the product entity to enrich
     * @return the enriched product entity with prices loaded
     */
    private ProductEntity enrichProduct(ProductEntity product) {
        product.setPrices(loadPricesByProductId(product.getId()));
        return product;
    }

    /**
     * Loads prices for a specific product.
     */
    private List<PriceEntity> loadPricesByProductId(Long productId) {
        return jdbcTemplate.query(
                SELECT_PRICES_BY_PRODUCT,
                priceRowMapper,
                productId
        );
    }

    /**
     * Maps a ResultSet row to a PriceEntity.
     *
     * @param rs the ResultSet containing price data
     * @return a PriceEntity populated with data from the ResultSet
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private PriceEntity mapPriceRow(ResultSet rs) throws SQLException {
        PriceEntity price = new PriceEntity();
        price.setId(rs.getLong("id"));
        price.setValue(rs.getBigDecimal("price_value"));
        price.setInitDate(rs.getDate("init_date").toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            price.setEndDate(endDate.toLocalDate());
        }

        return price;
    }

    /**
     * Maps a ResultSet row to a ProductEntity.
     */
    private ProductEntity mapProductRow(ResultSet rs) throws SQLException {
        ProductEntity product = new ProductEntity();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        return product;
    }

}
