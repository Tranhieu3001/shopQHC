package com.example.shopQHC.controller;

import com.example.shopQHC.entity.Category;
import com.example.shopQHC.entity.Product;
import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.repository.ProductSizeRepository;
import com.example.shopQHC.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductSizeRepository productSizeRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String showProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {

        List<Category> parentCategories = categoryRepository.findByParentIsNull();
        Map<Long, List<Category>> childCategoryMap = new LinkedHashMap<>();

        for (Category parent : parentCategories) {
            childCategoryMap.put(parent.getId(), categoryRepository.findByParentId(parent.getId()));
        }

        model.addAttribute("products", productService.filterProducts(keyword, categoryId));
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategoryMap", childCategoryMap);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        return "user/products";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        List<Category> parentCategories = categoryRepository.findByParentIsNull();
        Map<Long, List<Category>> childCategoryMap = new LinkedHashMap<>();

        for (Category parent : parentCategories) {
            childCategoryMap.put(parent.getId(), categoryRepository.findByParentId(parent.getId()));
        }

        model.addAttribute("product", product);
        model.addAttribute("productSizes", productSizeRepository.findByProductId(id));
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategoryMap", childCategoryMap);

        return "user/product-detail";
    }
}