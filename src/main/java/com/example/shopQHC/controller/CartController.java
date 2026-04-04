package com.example.shopQHC.controller;

import com.example.shopQHC.entity.Cart;
import com.example.shopQHC.entity.CartItem;
import com.example.shopQHC.entity.Product;
import com.example.shopQHC.entity.ProductSize;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.repository.CartItemRepository;
import com.example.shopQHC.repository.CartRepository;
import com.example.shopQHC.repository.ProductRepository;
import com.example.shopQHC.repository.ProductSizeRepository;
import com.example.shopQHC.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSizeRepository productSizeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          ProductSizeRepository productSizeRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productSizeRepository = productSizeRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng đang đăng nhập"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCartItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "user/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productSizeId") Long productSizeId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            Authentication authentication) {

        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }

        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        ProductSize productSize = productSizeRepository.findById(productSizeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy size sản phẩm"));

        CartItem cartItem = cartItemRepository.findByCartAndProductSize(cart, productSize)
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductSize(productSize);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(productSize.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        return "redirect:/cart";
    }

    @PostMapping("/add-product")
    public String addProductToCart(@RequestParam("productId") Long productId,
                                   @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                   Authentication authentication) {

        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }

        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductSize productSize = productSizeRepository.findByProductId(product.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm này chưa có size"));

        CartItem cartItem = cartItemRepository.findByCartAndProductSize(cart, productSize)
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductSize(productSize);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(productSize.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("cartItemId") Long cartItemId,
                             @RequestParam("quantity") Integer quantity,
                             Authentication authentication) {

        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa giỏ hàng này");
        }

        if (quantity == null || quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return "redirect:/cart";
    }

    @GetMapping("/remove/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId,
                                 Authentication authentication) {

        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa sản phẩm này");
        }

        cartItemRepository.delete(cartItem);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(cartItems);

        return "redirect:/cart";
    }
}