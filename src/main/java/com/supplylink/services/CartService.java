package com.supplylink.services;

import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.models.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CartService {

    CartItem addToCart(UUID userId, CartItemReqDTO request);

    CartItem updateCartItem(UUID userId, UUID productId, int quantity);

    void removeFromCart(UUID userId, UUID productId);

    List<CartItem> getCartItems(UUID userId);

    void clearCart(UUID userId);
}