package com.supplylink.dtos.req;

import com.supplylink.models.ShippingAddress;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderReqDTO {
    @NotNull
    private List<CartItemReqDTO> items;

    @NotNull
    private ShippingAddress shippingAddress;

    public @NotNull List<CartItemReqDTO> getItems() {
        return items;
    }

    public void setItems(@NotNull List<CartItemReqDTO> items) {
        this.items = items;
    }

    public @NotNull ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(@NotNull ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
