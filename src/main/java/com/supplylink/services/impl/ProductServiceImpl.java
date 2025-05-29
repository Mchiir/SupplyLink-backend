package com.supplylink.services.impl;

import com.supplylink.models.Product;
import com.supplylink.repositories.ProductRepository;
import com.supplylink.services.ProductService;
import com.supplylink.specifications.ProductSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
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

    @Transactional
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
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setCurrency(updated.getCurrency());
        product.setQuantity(updated.getQuantity());
        product.setRating(updated.getRating());
        product.setCategory(updated.getCategory());
        product.setLocation(updated.getLocation());
        return productRepository.save(product);
    }

    @Override
    public boolean deleteProduct(UUID id) {
        productRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<Product> searchProducts(
            UUID categoryId,
            UUID locationId,
            Double minPrice,
            Double maxPrice,
            String keyword,
            Integer minQuantity,
            Date createdAfter,
            Double minRating,
            Pageable pageable) {

        Specification<Product> spec = Specification
                .where(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.hasLocation(locationId))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice))
                .and(ProductSpecification.keywordContains(keyword))
                .and(minQuantity != null ? ProductSpecification.hasMinimumQuantity(minQuantity) : null)
                .and(createdAfter != null ? ProductSpecification.createdAfter(createdAfter) : null)
                .and(ProductSpecification.hasMinRating(minRating));

        return productRepository.findAll(spec, pageable);
    }
}