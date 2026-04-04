package com.example.shopQHC.service.impl;

import com.example.shopQHC.entity.Category;
import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục id = " + id));
    }

    @Override
    public Category createCategory(Category category) {
        String name = category.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category existing = getCategoryById(id);

        String name = category.getName().trim();
        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        existing.setName(name);
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}