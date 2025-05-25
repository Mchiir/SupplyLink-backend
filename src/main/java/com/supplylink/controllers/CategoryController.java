package com.supplylink.controllers;

import com.supplylink.dtos.req.CategoryReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.CategoryResDTO;
import com.supplylink.models.Category;
import com.supplylink.services.CategoryService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResDTO>>> getAllCategories() {
        var categories = categoryService.getAllCategories()
                .stream()
                .map(c -> modelMapper.map(c, CategoryResDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Fetched successfully", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResDTO>> getCategoryById(@PathVariable UUID id) {
        var category = categoryService.getCategoryById(id);
        if (category == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Category not found"));
        return ResponseEntity.ok(ApiResponse.success("Fetched", modelMapper.map(category, CategoryResDTO.class)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResDTO>> createCategory(@Valid @RequestBody CategoryReqDTO dto) {
        try {
            var created = categoryService.createCategory(modelMapper.map(dto, Category.class));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Created successfully", modelMapper.map(created, CategoryResDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Failed: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResDTO>> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryReqDTO dto) {
        try {
            var updated = categoryService.updateCategory(id, modelMapper.map(dto, Category.class));
            if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
            return ResponseEntity.ok(ApiResponse.success("Updated", modelMapper.map(updated, CategoryResDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        if (categoryService.deleteCategory(id)) {
            return ResponseEntity.ok(ApiResponse.success("Deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found"));
    }
}
