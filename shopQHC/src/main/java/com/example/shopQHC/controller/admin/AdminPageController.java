package com.example.shopQHC.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @RequestMapping
    public class AdminCategoryController{}
    //@GetMapping("/categories")
    //public String categories() {
    //    return "admin/categories";
    //}

    //@GetMapping("/products")
    //public String products() {
    //    return "admin/products";
    //}
    @RequestMapping
    public  class AdminProductController {}

    //@GetMapping("/users")
    //public String users() {
    //    return "admin/users";
    //}
    @RequestMapping
    public  class AdminUserController {}

    //@GetMapping("/orders")
    //public String orders() {
        //return "admin/orders";
    //}
    @RequestMapping
    public class AdminOrderController {}

    //@GetMapping("/coupons")
    //public String coupons() {
    //    return "admin/coupons";
    //}
    @RequestMapping
    public class AdminCouponContronller {}

    //@GetMapping("/statistics")
    //public String statistics() {
    //    return "admin/statistics";
    //}
    @RequestMapping
    public class AdminStaticsController {}

}