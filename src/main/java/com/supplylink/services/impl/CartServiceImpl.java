package com.supplylink.services.impl;

import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.models.CartItem;
import com.supplylink.models.Product;
import com.supplylink.models.User;
import com.supplylink.repositories.CartItemRepository;
import com.supplylink.repositories.ProductRepository;
import com.supplylink.repositories.UserRepository;
import com.supplylink.services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartItem addToCart(UUID userId, CartItemReqDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        CartItem cartItem;
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setUpdatedAt(new Date());
            cartItem = existing;
        } else {
            cartItem = new CartItem(user, product, request.getQuantity());
            Date now = new Date();
            cartItem.setCreatedAt(now);
            cartItem.setCurrency(product.getCurrency());
        }

        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem updateCartItem(UUID userId, UUID productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        item.setUpdatedAt(new Date());
        return cartItemRepository.save(item);
    }

    @Override
    public void removeFromCart(UUID userId, UUID productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<CartItem> getCartItems(UUID userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public void clearCart(UUID userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }
}