package com.supplylink.services.impl;

import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.dtos.res.PaymentResponse;
import com.supplylink.models.CartItem;
import com.supplylink.models.Order;
import com.supplylink.models.OrderItem;
import com.supplylink.models.User;
import com.supplylink.models.enums.OrderStatus;
import com.supplylink.models.enums.PaymentStatus;
import com.supplylink.repositories.*;
import com.supplylink.services.OrderService;
import com.supplylink.services.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            UserRepository userRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Order checkout(UUID userId, OrderReqDTO request, PaymentService paymentService) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        String currency = request.getCurrency();
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentResponse paymentResponse = paymentService.processPayment(userId, totalAmount, currency);
        if (!PaymentStatus.SUCCEEDED.equals(paymentResponse.getStatus())) {
            throw new RuntimeException("Payment failed");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Add order items to the order
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem(order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice(), currency);
            order.getItems().add(orderItem); // Ensure proper relationship
        }

        order.setTotalAmount(totalAmount);
        order.setCurrency(currency);
        order.setShippingAddress(request.getShippingAddress());

        order = orderRepository.save(order); // Cascade saves order items

        cartItemRepository.deleteAllByUserId(userId); // now clear cart
        return order;
    }

    @Override
    public Page<Order> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public Order getOrderDetails(UUID orderId, UUID userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not authorized to cancel this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}