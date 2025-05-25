package com.supplylink.dtos.res;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductResDTO {

    private UUID Id;
    private String Name;
    private String Description;
    private BigDecimal Price;
    private Integer Quantity;
    private UUID CategoryId;
    private UUID LocationId;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal price) {
        Price = price;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }

    public UUID getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(UUID categoryId) {
        CategoryId = categoryId;
    }

    public UUID getLocationId() {
        return LocationId;
    }

    public void setLocationId(UUID locationId) {
        LocationId = locationId;
    }
}