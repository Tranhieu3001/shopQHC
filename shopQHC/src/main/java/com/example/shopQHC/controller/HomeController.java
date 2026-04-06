package com.example.shopQHC.controller;

import com.example.shopQHC.repository.CategoryRepository;
import com.example.shopQHC.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("newProducts", productService.getNewestProducts());
        model.addAttribute("bestSellerProducts", productService.getBestSellerProducts());
        model.addAttribute("parentCategories", categoryRepository.findByParentIsNull());

        List<String> bannerImages = List.of(
                "/images/banner1.png",
                "/images/banner2.jpg",
                "/images/banner3.jpg"
        );
        model.addAttribute("bannerImages", bannerImages);
        return "index";
    }
}