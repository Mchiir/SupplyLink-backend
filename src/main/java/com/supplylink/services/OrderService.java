package com.supplylink.services;

import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.models.Order;
import com.supplylink.models.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {
    Order checkout(UUID userId, OrderReqDTO request, PaymentService paymentService);

    List<Order> getUserOrders(UUID userId);
    Order getOrderDetails(UUID orderId, UUID userId);
    void updateOrderStatus(UUID orderId, OrderStatus status);
    void cancelOrder(UUID orderId, UUID userId);
}
