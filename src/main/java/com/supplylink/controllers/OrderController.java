package com.supplylink.controllers;

import com.supplylink.context.ContextAccessor;
import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.OrderItemResDTO;
import com.supplylink.dtos.res.OrderResDTO;
import com.supplylink.models.Order;
import com.supplylink.models.enums.OrderStatus;
import com.supplylink.services.OrderService;
import com.supplylink.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ContextAccessor contextAccessor;

    @Autowired
    @Qualifier("stripePaymentService")
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResDTO>> checkout(@Valid @RequestBody OrderReqDTO request,
        HttpServletRequest httpRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpRequest);
            OrderResDTO res = mapToResDTO(orderService.checkout(currentUserId, request, paymentService));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Order placed successfully", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Checkout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<OrderResDTO>>> getUserOrders(HttpServletRequest request) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            List<Order> orders = orderService.getUserOrders(userId);
            List<OrderResDTO> response = orders.stream().map(this::mapToResDTO).toList();
            return ResponseEntity.ok(ApiResponse.success("Orders fetched", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResDTO>> getOrderDetails(@PathVariable UUID orderId, HttpServletRequest request) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            Order order = orderService.getOrderDetails(orderId, userId);
            return ResponseEntity.ok(ApiResponse.success("Order details", mapToResDTO(order)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('SYSTEM') OR hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable UUID orderId, @RequestParam OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable UUID orderId, HttpServletRequest request) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully",null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Cancellation failed: " + e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Unexpected error: " + e.getMessage()));
        }
    }

    private OrderResDTO mapToResDTO(Order order) {
        List<OrderItemResDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemResDTO dto = new OrderItemResDTO();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setCurrency(item.getCurrency());
            dto.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return dto;
        }).collect(Collectors.toList());

        OrderResDTO dto = new OrderResDTO();
        dto.setOrderId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setItems(itemDTOs); // from above
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCurrency(order.getCurrency());
        return dto;
    }
}