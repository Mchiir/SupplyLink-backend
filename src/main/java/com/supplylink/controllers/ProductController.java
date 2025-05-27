package com.supplylink.controllers;

import com.supplylink.dtos.req.ProductReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.ProductResDTO;
import com.supplylink.models.Category;
import com.supplylink.models.Location;
import com.supplylink.models.Product;
import com.supplylink.services.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResDTO>>> searchProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        try {
            Page<Product> products = productService.searchProducts(categoryId, locationId, minPrice, maxPrice, keyword, pageable);
            Page<ProductResDTO> dtoPage = products.map(p -> modelMapper.map(p, ProductResDTO.class));
            return ResponseEntity.ok(ApiResponse.success("Search results", dtoPage));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductResDTO>>> createProducts(@Valid @RequestBody List<ProductReqDTO> dtos) {
        try {
            List<Product> products = dtos.stream().map(dto -> {
                Product product = new Product();
                product.setName(dto.getName());
                product.setDescription(dto.getDescription());
                product.setPrice(dto.getPrice());
                product.setQuantity(dto.getQuantity());

                // Create category and location objects with only the ID set
                Category category = new Category();
                category.setId(dto.getCategoryId());
                product.setCategory(category);

                Location location = new Location();
                location.setId(dto.getLocationId());
                product.setLocation(location);

                return product;
            }).collect(Collectors.toList());

            List<ProductResDTO> created = productService.createProducts(products).stream().map(p -> {
                ProductResDTO res = new ProductResDTO();
                res.setId(p.getId());
                res.setName(p.getName());
                res.setDescription(p.getDescription());
                res.setPrice(p.getPrice());
                res.setQuantity(p.getQuantity());
                res.setCategoryId(p.getCategory().getId());
                res.setLocationId(p.getLocation().getId());
                return res;
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Batch created", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Batch failed: " + e.getMessage()));
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
