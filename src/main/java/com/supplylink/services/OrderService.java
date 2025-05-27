package com.supplylink.services;

import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.dtos.res.OrderResDTO;
import com.supplylink.models.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {

    // Converts all cart items into an order
    OrderResDTO checkout(UUID userId, OrderReqDTO request, PaymentService paymentService);

    // Returns all past orders of a user
    List<OrderResDTO> getUserOrders(UUID userId);

    // Returns full details of a single order
    OrderResDTO getOrderDetails(UUID orderId, UUID userId);

    // Updates the status of an order (admin or system)
    void updateOrderStatus(UUID orderId, OrderStatus status);
}
