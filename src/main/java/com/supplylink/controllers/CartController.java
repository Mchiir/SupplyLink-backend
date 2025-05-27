package com.supplylink.controllers;

import com.supplylink.context.ContextAccessor;
import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.CartItemResDTO;
import com.supplylink.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ContextAccessor contextAccessor;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResDTO>> addToCart(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody CartItemReqDTO request) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpServletRequest);
            CartItemResDTO res = cartService.addToCart(currentUserId, request);
            return ResponseEntity.ok(ApiResponse.success("Item added to cart", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to add item: " + e.getMessage()));
        }
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartItemResDTO>> updateCartItem(
            HttpServletRequest httpServletRequest,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpServletRequest);
            CartItemResDTO res = cartService.updateCartItem(currentUserId, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Cart item updated", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            HttpServletRequest httpServletRequest,
            @PathVariable UUID productId) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpServletRequest);
            cartService.removeFromCart(currentUserId, productId);
            return ResponseEntity.ok(ApiResponse.success("Item removed", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Remove failed: " + e.getMessage()));
        }
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItemResDTO>>> getCartItems(
            HttpServletRequest httpServletRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpServletRequest);
            List<CartItemResDTO> items = cartService.getCartItems(currentUserId);
            return ResponseEntity.ok(ApiResponse.success("Fetched cart items", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get cart items: " + e.getMessage()));
        }
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<String>> clearCart(
            HttpServletRequest httpServletRequest) {
        try {
            UUID currentUserId = contextAccessor.getCurrentUserId(httpServletRequest);
            cartService.clearCart(currentUserId);
            return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Clear failed: " + e.getMessage()));
        }
    }
}
