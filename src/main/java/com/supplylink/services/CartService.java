package com.supplylink.services;

import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.dtos.res.CartItemResDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CartService {

    CartItemResDTO addToCart(UUID userId, CartItemReqDTO request);

    // Updates quantity of a cart item
    CartItemResDTO updateCartItem(UUID userId, UUID productId, int quantity);

    // Removes a specific product from the user's cart
    void removeFromCart(UUID userId, UUID productId);

    // Returns all cart items for the user
    List<CartItemResDTO> getCartItems(UUID userId);

    // Clears all cart items for a user
    void clearCart(UUID userId);
}
