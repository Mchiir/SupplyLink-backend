package com.supplylink.services.impl;

import com.supplylink.models.Category;
import com.supplylink.repositories.CategoryRepository;
import com.supplylink.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> createCategories(List<Category> categories) {
        return categoryRepository.saveAll(categories);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(UUID id, Category updated) {
        Category category = getCategoryById(id);
        category.setName(updated.getName());
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
        return true;
    }
}