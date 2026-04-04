package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Cart;
import com.example.shopQHC.entity.CartItem;
import com.example.shopQHC.entity.ProductSize;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartAndProductSize(Cart cart, ProductSize productSize);
    @Transactional
    void deleteByCartId(Long cartId);
}