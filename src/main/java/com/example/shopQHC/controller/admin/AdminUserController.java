package com.example.shopQHC.controller;

import com.example.shopQHC.dto.request.UserRequest;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        UserRequest request = new UserRequest();
        request.setRole(User.Role.CUSTOMER);
        request.setStatus(User.Status.ACTIVE);

        model.addAttribute("userRequest", request);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("statuses", User.Status.values());
        model.addAttribute("pageTitle", "Thêm tài khoản");
        model.addAttribute("formAction", "/admin/users/create");
        return "admin/user-form";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("userRequest") UserRequest request,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("statuses", User.Status.values());
            model.addAttribute("pageTitle", "Thêm tài khoản");
            model.addAttribute("formAction", "/admin/users/create");
            return "admin/user-form";
        }

        try {
            userService.createUser(request);
            return "redirect:/admin/users?success=create";
        } catch (IllegalArgumentException e) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("statuses", User.Status.values());
            model.addAttribute("pageTitle", "Thêm tài khoản");
            model.addAttribute("formAction", "/admin/users/create");
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/user-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);

        UserRequest request = new UserRequest();
        request.setFullName(user.getFullName());
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setPhone(user.getPhone());
        request.setAddress(user.getAddress());
        request.setRole(user.getRole());
        request.setStatus(user.getStatus());

        model.addAttribute("userRequest", request);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("statuses", User.Status.values());
        model.addAttribute("pageTitle", "Cập nhật tài khoản");
        model.addAttribute("formAction", "/admin/users/edit/" + id);
        return "admin/user-form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userRequest") UserRequest request,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("statuses", User.Status.values());
            model.addAttribute("pageTitle", "Cập nhật tài khoản");
            model.addAttribute("formAction", "/admin/users/edit/" + id);
            return "admin/user-form";
        }

        try {
            userService.updateUser(id, request);
            return "redirect:/admin/users?success=update";
        } catch (IllegalArgumentException e) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("statuses", User.Status.values());
            model.addAttribute("pageTitle", "Cập nhật tài khoản");
            model.addAttribute("formAction", "/admin/users/edit/" + id);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/user-form";
        }
    }
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users?success=delete";
    }
}