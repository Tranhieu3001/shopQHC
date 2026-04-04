package com.example.shopQHC.controller.admin;

import com.example.shopQHC.entity.Category;
import com.example.shopQHC.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/categories";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Thêm danh mục");
        return "admin/categories";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute("category") Category category,
                                 Model model) {
        try {
            categoryService.createCategory(category);
            return "redirect:/admin/categories?success=add";
        } catch (IllegalArgumentException e) {
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Thêm danh mục");
            model.addAttribute("error", e.getMessage());
            return "admin/categories";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        model.addAttribute("pageTitle", "Cập nhật danh mục");
        return "admin/categories";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute("category") Category category,
                                 Model model) {
        try {
            categoryService.updateCategory(id, category);
            return "redirect:/admin/categories?success=update";
        } catch (IllegalArgumentException e) {
            category.setId(id);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Cập nhật danh mục");
            model.addAttribute("error", e.getMessage());
            return "admin/categories";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories?success=delete";
    }
}