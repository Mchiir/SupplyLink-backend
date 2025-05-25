package com.supplylink.controllers;

import com.supplylink.models.Product;
import com.supplylink.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        return Optional.ofNullable(productService.getProductById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PostMapping("/batch")
    public List<Product> createProducts(@RequestBody List<Product> products) {
        // Optional batch create endpoint for multiple products at once
        return productService.createProducts(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return Optional.ofNullable(productService.updateProduct(id, product))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
