package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(Product.Status status);
    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, Product.Status status);
    List<Product> findByCategoryIdAndStatus(Long categoryId, Product.Status status);
    List<Product> findByIsNewTrueAndStatus(Product.Status status);
    List<Product> findByCategoryIdInAndStatus(List<Long> categoryIds, Product.Status status);
    List<Product> findByNameContainingIgnoreCaseAndCategoryIdInAndStatus(String keyword, List<Long> categoryIds, Product.Status status);

    // ================== THỐNG KÊ ==================

    long countByStatus(Product.Status status);

    List<Product> findTop5ByStatusOrderBySoldQuantityDesc(Product.Status status);

    List<Product> findTop10ByStatusOrderBySoldQuantityDesc(Product.Status status);
}