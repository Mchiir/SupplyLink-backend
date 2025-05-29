package com.supplylink.dtos.req;

import com.supplylink.models.ShippingAddress;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderReqDTO {
    @NotNull(message = "Order items are required")
    private List<CartItemReqDTO> items;

    @NotNull(message = "Shipping address is required")
    private ShippingAddress shippingAddress;

    @NotNull(message = "Currency is required")
    private String currency;

    public List<CartItemReqDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemReqDTO> items) {
        this.items = items;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
