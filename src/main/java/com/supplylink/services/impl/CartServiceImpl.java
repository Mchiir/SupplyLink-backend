package com.supplylink.services.impl;

import com.supplylink.dtos.req.CartItemReqDTO;
import com.supplylink.dtos.res.CartItemResDTO;
import com.supplylink.models.CartItem;
import com.supplylink.models.Product;
import com.supplylink.models.User;
import com.supplylink.repositories.CartItemRepository;
import com.supplylink.repositories.ProductRepository;
import com.supplylink.repositories.UserRepository;
import com.supplylink.services.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartItemResDTO addToCart(UUID userId, CartItemReqDTO request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
            .orElse(null);

        CartItem cartItem;
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItem = existing;
        } else {
            cartItem = new CartItem(user, product, request.getQuantity());
        }

        CartItem saved = cartItemRepository.save(cartItem);
        return modelMapper.map(saved, CartItemResDTO.class);
    }

    @Override
    public CartItemResDTO updateCartItem(UUID userId, UUID productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        return modelMapper.map(cartItemRepository.save(item), CartItemResDTO.class);
    }

    @Override
    public void removeFromCart(UUID userId, UUID productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<CartItemResDTO> getCartItems(UUID userId) {
        return cartItemRepository.findByUserId(userId)
            .stream()
            .map(item -> modelMapper.map(item, CartItemResDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void clearCart(UUID userId) {
        cartItemRepository.deleteAllByUser(userId);
    }
}