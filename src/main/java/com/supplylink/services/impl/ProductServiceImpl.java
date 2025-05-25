package com.supplylink.services.impl;

import com.supplylink.models.Product;
import com.supplylink.repositories.ProductRepository;
import com.supplylink.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> createProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(UUID id, Product updated) {
        Product product = getProductById(id);
        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        product.setQuantity(updated.getQuantity());
        product.setCategory(updated.getCategory());
        product.setLocation(updated.getLocation());
        return productRepository.save(product);
    }

    @Override
    public boolean deleteProduct(UUID id) {
        productRepository.deleteById(id);
        return true;
    }
}