package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Product;
import com.example.shopQHC.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProduct(Product product);
    List<ProductSize> findByProductId(Long productId);
    Optional<ProductSize> findByProductIdAndSize(Long productId, String size);
}