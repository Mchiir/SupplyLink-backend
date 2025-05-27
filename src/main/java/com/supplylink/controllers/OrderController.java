package com.supplylink.controllers;

import com.supplylink.context.ContextAccessor;
import com.supplylink.dtos.req.OrderReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.OrderResDTO;
import com.supplylink.models.enums.OrderStatus;
import com.supplylink.services.OrderService;
import com.supplylink.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ContextAccessor contextAccessor;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResDTO>> checkout(@Valid @RequestBody OrderReqDTO request,
        HttpServletRequest httpRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpRequest);
            OrderResDTO res = orderService.checkout(currentUserId, request, paymentService);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Order placed successfully", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Checkout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<OrderResDTO>>> getUserOrders(HttpServletRequest httpRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpRequest);
            List<OrderResDTO> orders = orderService.getUserOrders(currentUserId);
            return ResponseEntity.ok(ApiResponse.success("Orders fetched", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResDTO>> getOrderDetails(
            @PathVariable UUID orderId, HttpServletRequest httpRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpRequest);
            OrderResDTO order = orderService.getOrderDetails(orderId, currentUserId);
            return ResponseEntity.ok(ApiResponse.success("Order details", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable UUID orderId, HttpServletRequest httpRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpRequest);
            orderService.cancelOrder(orderId, currentUserId);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Cancellation failed: " + e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Not authorized to cancel this order"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
}
