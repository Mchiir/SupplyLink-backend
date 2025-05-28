package com.supplylink.dtos.res;

import com.supplylink.models.ShippingAddress;
import com.supplylink.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderResDTO {
    private UUID orderId;
    private Date createdAt;
    private OrderStatus status;
    private List<OrderItemResDTO> items;
    private BigDecimal totalAmount;
    private ShippingAddress shippingAddress;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemResDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}