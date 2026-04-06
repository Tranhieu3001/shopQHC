package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Category;
import com.example.shopQHC.entity.Category.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameContainingIgnoreCase(String keyword);
    List<Category> findByStatus(Status status);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
}