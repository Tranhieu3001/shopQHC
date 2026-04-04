package com.example.shopQHC.controller;

import com.example.shopQHC.dto.request.ProductRequest;
import com.example.shopQHC.entity.Category;
import com.example.shopQHC.entity.Product;
import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public AdminProductController(ProductService productService,
                                  CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        ProductRequest request = new ProductRequest();
        request.setStatus(Product.Status.ACTIVE);
        request.setIsNew(false);
        request.setSoldQuantity(0);

        model.addAttribute("productRequest", request);
        model.addAttribute("categories", getActiveCategories());
        model.addAttribute("genders", Product.Gender.values());
        model.addAttribute("statuses", Product.Status.values());
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        model.addAttribute("formAction", "/admin/products/create");

        return "admin/form";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productRequest") ProductRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", getActiveCategories());
            model.addAttribute("genders", Product.Gender.values());
            model.addAttribute("statuses", Product.Status.values());
            model.addAttribute("pageTitle", "Thêm sản phẩm");
            model.addAttribute("formAction", "/admin/products/create");
            return "admin/form";
        }

        productService.createProduct(request);
        return "redirect:/admin/products?success=create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);

        ProductRequest request = new ProductRequest();
        request.setName(product.getName());
        request.setDescription(product.getDescription());
        request.setPrice(product.getPrice());
        request.setStockQuantity(product.getStockQuantity());
        request.setImageUrl(product.getImageUrl());
        request.setColor(product.getColor());
        request.setBrand(product.getBrand());
        request.setAgeGroup(product.getAgeGroup());
        request.setGender(product.getGender());
        request.setIsNew(product.getIsNew());
        request.setSoldQuantity(product.getSoldQuantity());
        request.setStatus(product.getStatus());
        request.setCategoryId(product.getCategory().getId());

        model.addAttribute("productRequest", request);
        model.addAttribute("categories", getActiveCategories());
        model.addAttribute("genders", Product.Gender.values());
        model.addAttribute("statuses", Product.Status.values());
        model.addAttribute("pageTitle", "Cập nhật sản phẩm");
        model.addAttribute("formAction", "/admin/products/edit/" + id);

        return "admin/form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productRequest") ProductRequest request,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", getActiveCategories());
            model.addAttribute("genders", Product.Gender.values());
            model.addAttribute("statuses", Product.Status.values());
            model.addAttribute("pageTitle", "Cập nhật sản phẩm");
            model.addAttribute("formAction", "/admin/products/edit/" + id);
            return "admin/form";
        }

        productService.updateProduct(id, request);
        return "redirect:/admin/products?success=update";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products?success=delete";
    }

    private List<Category> getActiveCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> category.getStatus() == Category.Status.ACTIVE)
                .toList();
    }
}