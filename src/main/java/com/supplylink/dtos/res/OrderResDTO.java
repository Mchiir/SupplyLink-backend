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
}
