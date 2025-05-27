package com.supplylink.services;

import com.supplylink.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ProductService {
    Product createProduct(Product product);
    List<Product> createProducts(List<Product> products);
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    Product updateProduct(UUID id, Product product);
    boolean deleteProduct(UUID id);

    Page<Product> searchProducts(
            UUID categoryId,
            UUID locationId,
            Double minPrice,
            Double maxPrice,
            String keyword,
            Pageable pageable
    );
}