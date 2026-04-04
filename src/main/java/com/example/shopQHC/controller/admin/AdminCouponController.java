package com.example.shopQHC.controller.admin;

import com.example.shopQHC.entity.Coupon;
import com.example.shopQHC.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public String listCoupons(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Coupon.Status status,
            Model model
    ) {
        model.addAttribute("coupons", couponService.getAllCoupons(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", Coupon.Status.values());
        return "admin/coupons";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("discountTypes", Coupon.DiscountType.values());
        model.addAttribute("statuses", Coupon.Status.values());
        model.addAttribute("formAction", "/admin/coupons/create");
        model.addAttribute("pageTitle", "Thêm mã giảm giá");
        return "admin/coupon-form";
    }

    @PostMapping("/create")
    public String createCoupon(
            @ModelAttribute Coupon coupon,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            couponService.saveCoupon(coupon);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mã giảm giá thành công");
            return "redirect:/admin/coupons";
        } catch (IllegalArgumentException e) {
            model.addAttribute("coupon", coupon);
            model.addAttribute("discountTypes", Coupon.DiscountType.values());
            model.addAttribute("statuses", Coupon.Status.values());
            model.addAttribute("formAction", "/admin/coupons/create");
            model.addAttribute("pageTitle", "Thêm mã giảm giá");
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/coupon-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("coupon", couponService.getCouponById(id));
        model.addAttribute("discountTypes", Coupon.DiscountType.values());
        model.addAttribute("statuses", Coupon.Status.values());
        model.addAttribute("formAction", "/admin/coupons/edit/" + id);
        model.addAttribute("pageTitle", "Cập nhật mã giảm giá");
        return "admin/coupon-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCoupon(
            @PathVariable Long id,
            @ModelAttribute Coupon coupon,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            couponService.updateCoupon(id, coupon);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mã giảm giá thành công");
            return "redirect:/admin/coupons";
        } catch (IllegalArgumentException e) {
            model.addAttribute("coupon", coupon);
            model.addAttribute("discountTypes", Coupon.DiscountType.values());
            model.addAttribute("statuses", Coupon.Status.values());
            model.addAttribute("formAction", "/admin/coupons/edit/" + id);
            model.addAttribute("pageTitle", "Cập nhật mã giảm giá");
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/coupon-form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Coupon.Status status,
            RedirectAttributes redirectAttributes
    ) {
        couponService.updateCouponStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        return "redirect:/admin/coupons";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/admin/coupons";
    }
}