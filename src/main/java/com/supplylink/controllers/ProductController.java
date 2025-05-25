package com.supplylink.controllers;

import com.supplylink.dtos.req.ProductReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.ProductResDTO;
import com.supplylink.models.Product;
import com.supplylink.services.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResDTO>>> getAllProducts() {
        var products = productService.getAllProducts()
                .stream()
                .map(p -> modelMapper.map(p, ProductResDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Fetched all", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResDTO>> getProductById(@PathVariable UUID id) {
        var product = productService.getProductById(id);
        if (product == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
        return ResponseEntity.ok(ApiResponse.success("Found", modelMapper.map(product, ProductResDTO.class)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResDTO>> createProduct(@Valid @RequestBody ProductReqDTO dto) {
        try {
            var created = productService.createProduct(modelMapper.map(dto, Product.class));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Created", modelMapper.map(created, ProductResDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Creation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<ProductResDTO>>> createProducts(@Valid @RequestBody List<ProductReqDTO> dtos) {
        try {
            var products = dtos.stream().map(dto -> modelMapper.map(dto, Product.class)).collect(Collectors.toList());
            var created = productService.createProducts(products)
                    .stream()
                    .map(p -> modelMapper.map(p, ProductResDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Batch created", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Batch failed: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResDTO>> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductReqDTO dto) {
        try {
            var updated = productService.updateProduct(id, modelMapper.map(dto, Product.class));
            if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
            return ResponseEntity.ok(ApiResponse.success("Updated", modelMapper.map(updated, ProductResDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.ok(ApiResponse.success("Deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
    }
}
