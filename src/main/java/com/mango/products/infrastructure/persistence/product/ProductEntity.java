package com.mango.products.infrastructure.persistence.product;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity for product persistence.
 */
public class ProductEntity {

    private Long id;
    private String name;
    private String description;
    private List<PriceEntity> prices;

    public ProductEntity() {
        this.prices = new ArrayList<>();
    }

    public ProductEntity(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prices = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PriceEntity> getPrices() {
        return prices;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrices(List<PriceEntity> prices) {
        this.prices = prices != null ? prices : new ArrayList<>();
    }

}
