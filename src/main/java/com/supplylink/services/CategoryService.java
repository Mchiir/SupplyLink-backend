package com.supplylink.services;

import com.supplylink.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {
    Category createCategory(Category category);
    List<Category> createCategories(List<Category> categories);
    Category getCategoryById(UUID id);
    List<Category> getAllCategories();
    Category updateCategory(UUID id, Category category);
    boolean deleteCategory(UUID id);
}