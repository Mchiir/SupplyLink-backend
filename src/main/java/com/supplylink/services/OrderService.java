package com.supplylink.services;

import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.models.Order;
import com.supplylink.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface OrderService {
    Order checkout(UUID userId, OrderReqDTO request, PaymentService paymentService);

    Page<Order> getUserOrders(UUID userId, Pageable pageable);
    Order getOrderDetails(UUID orderId, UUID userId);
    void updateOrderStatus(UUID orderId, OrderStatus status);
    void cancelOrder(UUID orderId, UUID userId);
}
