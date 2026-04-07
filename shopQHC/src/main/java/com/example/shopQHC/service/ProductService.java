package com.example.shopQHC.service;

import com.example.shopQHC.dto.request.ProductRequest;
import com.example.shopQHC.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(ProductRequest request);
    Product updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);

    List<Product> filterProducts(String keyword, Long categoryId);
    Product findById(Long id);
    List<Product> getNewestProducts();
    List<Product> getBestSellerProducts();

    // THỐNG KÊ 
    long countActiveProducts();
    List<Product> getTop5SellingProducts();
    List<Product> getTop10SellingProducts();
}