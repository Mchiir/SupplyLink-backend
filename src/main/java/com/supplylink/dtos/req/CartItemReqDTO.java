package com.supplylink.dtos.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public class CartItemReqDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    public @NotNull(message = "Product ID is required") UUID getProductId() {
        return productId;
    }

    public void setProductId(@NotNull(message = "Product ID is required") UUID productId) {
        this.productId = productId;
    }

    public @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be greater than zero") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Quantity is required") @Positive(message = "Quantity must be greater than zero") Integer quantity) {
        this.quantity = quantity;
    }
}
