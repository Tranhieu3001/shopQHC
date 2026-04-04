package com.example.shopQHC.service;

import com.example.shopQHC.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories(String keyword);
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}