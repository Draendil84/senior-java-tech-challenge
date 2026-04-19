package com.mango.products.infrastructure.persistence.product;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity for price persistence.
 */
public class PriceEntity {

    private Long id;
    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;
    private ProductEntity product;

    public PriceEntity() {
    }

    public PriceEntity(Long id, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.id = id;
        this.value = value;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setInitDate(LocalDate initDate) {
        this.initDate = initDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
