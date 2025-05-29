package com.supplylink.controllers;

import com.supplylink.dtos.req.ProductReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.ProductResDTO;
import com.supplylink.models.Category;
import com.supplylink.models.Location;
import com.supplylink.models.Product;
import com.supplylink.services.CategoryService;
import com.supplylink.services.LocationService;
import com.supplylink.services.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
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
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResDTO>>> getAllProducts() {
        List<ProductResDTO> products = productService.getAllProducts()
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
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Date createdAfter,
            @RequestParam(required = false) Double minRating,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<Product> products = productService.searchProducts(categoryId, locationId, minPrice, maxPrice, keyword, minQuantity, createdAfter,minRating, pageable);
        Page<ProductResDTO> dtoPage = products.map(p -> modelMapper.map(p, ProductResDTO.class));
        return ResponseEntity.ok(ApiResponse.success("Search results", dtoPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResDTO>> getProductById(@PathVariable UUID id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Found", modelMapper.map(product, ProductResDTO.class)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResDTO>> createProduct(@Valid @RequestBody ProductReqDTO dto) {
        try {
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());

            // Manually resolve related entities
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            Location location = locationService.getLocationById(dto.getLocationId());
            product.setCategory(category);
            product.setLocation(location);

            product.setRating(dto.getRating());
            product.setCurrency(dto.getCurrency());

            var created = productService.createProduct(product);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Created", modelMapper.map(created, ProductResDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Creation failed: " + e.getMessage()));
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

                Category category = categoryService.getCategoryById(dto.getCategoryId());
                Location location = locationService.getLocationById(dto.getLocationId());
                product.setCategory(category);
                product.setLocation(location);

                product.setRating(dto.getRating());
                product.setCurrency(dto.getCurrency());

                return product;
            }).collect(Collectors.toList());

            List<ProductResDTO> created = productService.createProducts(products).stream().map(p -> {
                ProductResDTO res = modelMapper.map(p, ProductResDTO.class);
                return res;
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Batch created", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Batch creation failed: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResDTO>> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductReqDTO dto) {
        try {
            Product product = new Product();
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());

            // Manually set category and location
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            Location location = locationService.getLocationById(dto.getLocationId());
            product.setCategory(category);
            product.setLocation(location);

            product.setRating(dto.getRating());
            product.setCurrency(dto.getCurrency());

            var updated = productService.updateProduct(id, product);

            return ResponseEntity.ok(
                    ApiResponse.success("Updated", modelMapper.map(updated, ProductResDTO.class))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.ok(ApiResponse.success("Deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Product not found"));
    }
}
