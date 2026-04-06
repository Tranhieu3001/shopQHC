package com.example.shopQHC.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/admin/data")
    public String getAdminData() {
        return "<h1 style='color:red'>Trang này chỉ dành cho ADMIN. Vui lòng đăng nhập</h1>";
    }

    @GetMapping("/com/example/shopQHC/controller/user/info")
    public String getUserInfo() {
        return "<h3 style='color:blue'>CHÀO MỪNG ĐẾN VỚI WEBSITE</h3>";
    }

    @GetMapping("/public/hello")
    public String publicPage() {
        return "Đây là trang public, ai cũng truy cập được";
    }
}