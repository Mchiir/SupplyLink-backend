package com.supplylink.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductReqDTO {

    @NotBlank(message = "Name is mandatory")
    private String Name;

    @NotBlank(message = "Description is mandatory")
    private String Description;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private BigDecimal Price;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be positive")
    private Integer Quantity;

    @NotNull(message = "CategoryId is mandatory")
    private UUID CategoryId;

    @NotNull(message = "LocationId is mandatory")
    private UUID LocationId;

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