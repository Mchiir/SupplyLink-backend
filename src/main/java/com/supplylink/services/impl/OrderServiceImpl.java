package com.supplylink.services.impl;

import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.dtos.res.OrderResDTO;
import com.supplylink.dtos.res.PaymentResponse;
import com.supplylink.models.CartItem;
import com.supplylink.models.Order;
import com.supplylink.models.OrderItem;
import com.supplylink.models.User;
import com.supplylink.models.enums.OrderStatus;
import com.supplylink.repositories.*;
import com.supplylink.services.OrderService;
import com.supplylink.services.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            ModelMapper modelMapper){
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderResDTO checkout(UUID userId, OrderReqDTO request, PaymentService paymentService) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
            .map(item -> BigDecimal.valueOf(item.getProduct().getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentResponse paymentResponse = paymentService.processPayment(userId, totalAmount);
        if (!"SUCCESS".equals(paymentResponse.getStatus())) {
            throw new RuntimeException("Payment failed");
        }

        Order order = new Order(user, totalAmount, OrderStatus.PENDING, request.getShippingAddress());
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem(order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAllByUserId(userId);

        return modelMapper.map(order, OrderResDTO.class);
    }

    @Override
    public List<OrderResDTO> getUserOrders(UUID userId) {
        return orderRepository.findByUserId(userId)
            .stream()
            .map(order -> modelMapper.map(order, OrderResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OrderResDTO getOrderDetails(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderResDTO.class);
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}