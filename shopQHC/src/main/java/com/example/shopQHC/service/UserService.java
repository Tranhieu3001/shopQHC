package com.example.shopQHC.service;

import com.example.shopQHC.dto.request.UserRequest;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.dto.request.RegisterRequest;
import com.example.shopQHC.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    UserResponse register(RegisterRequest request);
    User createUser(UserRequest request);
    User updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}