package com.supplylink.controllers;

import com.supplylink.context.ContextAccessor;
import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.CartItemResDTO;
import com.supplylink.models.CartItem;
import com.supplylink.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ContextAccessor contextAccessor;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemResDTO>> addToCart(
            HttpServletRequest request,
            @Valid @RequestBody CartItemReqDTO dto) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            CartItem entity = cartService.addToCart(userId, dto);
            CartItemResDTO responseDto = toDTO(entity);
            return ResponseEntity.ok(ApiResponse.success("Item added to cart", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to add item: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<CartItemResDTO>> updateCartItem(
            HttpServletRequest request,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            CartItem entity = cartService.updateCartItem(userId, productId, quantity);
            CartItemResDTO dto = toDTO(entity);
            return ResponseEntity.ok(ApiResponse.success("Cart item updated", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            HttpServletRequest request,
            @PathVariable UUID productId) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok(ApiResponse.success("Item removed", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Remove failed: " + e.getMessage()));
        }
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItemResDTO>>> getCartItems(
            HttpServletRequest request) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            List<CartItem> entities = cartService.getCartItems(userId);
            List<CartItemResDTO> dtos = entities.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Fetched cart items", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get cart items: " + e.getMessage()));
        }
    }

    @DeleteMapping("/clear-cart")
    public ResponseEntity<ApiResponse<String>> clearCart(HttpServletRequest request) {
        try {
            UUID userId = contextAccessor.getCurrentUserId(request);
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Clear failed: " + e.getMessage()));
        }
    }

    // Mapping helper
    private CartItemResDTO toDTO(CartItem entity) {
        CartItemResDTO dto = new CartItemResDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setProductName(entity.getProduct().getName());
        dto.setPrice(entity.getProduct().getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setTotal(entity.getProduct().getPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
        return dto;
    }
}
