package com.example.shopQHC.service.impl;

import com.example.shopQHC.dto.request.ProductRequest;
import com.example.shopQHC.entity.Category;
import com.example.shopQHC.entity.Product;
import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.repository.ProductRepository;
import com.example.shopQHC.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm id = " + id));
    }

    @Override
    public Product createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .color(request.getColor())
                .brand(request.getBrand())
                .ageGroup(request.getAgeGroup())
                .gender(request.getGender())
                .isNew(request.getIsNew())
                .soldQuantity(request.getSoldQuantity() != null ? request.getSoldQuantity() : 0)
                .status(request.getStatus())
                .category(category)
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {

        Product product = getProductById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setColor(request.getColor());
        product.setBrand(request.getBrand());
        product.setAgeGroup(request.getAgeGroup());
        product.setGender(request.getGender());
        product.setIsNew(request.getIsNew());
        product.setSoldQuantity(request.getSoldQuantity());
        product.setStatus(request.getStatus());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    @Override
    public List<Product> filterProducts(String keyword, Long categoryId) {

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasCategory = categoryId != null;

        if (hasKeyword && hasCategory) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryIdInAndStatus(
                    keyword, List.of(categoryId), Product.Status.ACTIVE);
        }

        if (hasKeyword) {
            return productRepository.findByNameContainingIgnoreCaseAndStatus(
                    keyword, Product.Status.ACTIVE);
        }

        if (hasCategory) {
            return productRepository.findByCategoryIdAndStatus(
                    categoryId, Product.Status.ACTIVE);
        }

        return productRepository.findByStatus(Product.Status.ACTIVE);
    }

    @Override
    public Product findById(Long id) {
        return getProductById(id);
    }

    // ================== USER ==================

    @Override
    public List<Product> getNewestProducts() {
        return productRepository.findByIsNewTrueAndStatus(Product.Status.ACTIVE);
    }

    @Override
    public List<Product> getBestSellerProducts() {
        return productRepository.findTop5ByStatusOrderBySoldQuantityDesc(Product.Status.ACTIVE);
    }

    // ================== THỐNG KÊ ==================

    @Override
    public long countActiveProducts() {
        return productRepository.countByStatus(Product.Status.ACTIVE);
    }

    @Override
    public List<Product> getTop5SellingProducts() {
        return productRepository.findTop5ByStatusOrderBySoldQuantityDesc(Product.Status.ACTIVE);
    }

    @Override
    public List<Product> getTop10SellingProducts() {
        return productRepository.findTop10ByStatusOrderBySoldQuantityDesc(Product.Status.ACTIVE);
    }
}