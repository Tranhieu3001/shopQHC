package com.example.shopQHC.controller;

import com.example.shopQHC.dto.CheckoutRequest;
import com.example.shopQHC.entity.Cart;
import com.example.shopQHC.entity.CartItem;
import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.repository.CartItemRepository;
import com.example.shopQHC.repository.CartRepository;
import com.example.shopQHC.repository.UserRepository;
import com.example.shopQHC.service.CheckoutService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CheckoutService checkoutService;

    public CheckoutController(CartRepository cartRepository,
                              CartItemRepository cartItemRepository,
                              UserRepository userRepository,
                              CheckoutService checkoutService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.checkoutService = checkoutService;
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

    @GetMapping("/checkout")
    public String checkoutPage(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setReceiverName(user.getFullName());
        checkoutRequest.setReceiverPhone(user.getPhone());
        checkoutRequest.setShippingAddress(user.getAddress());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("user", user);
        model.addAttribute("checkoutRequest", checkoutRequest);
        model.addAttribute("paymentMethods", Order.PaymentMethod.values());

        return "user/checkout";
    }

    //@PostMapping("/checkout")
    ///public String placeOrder(@ModelAttribute("checkoutRequest") CheckoutRequest request,
    ///                         Authentication authentication,
    ///                         RedirectAttributes redirectAttributes) {
///
      ///  try {
       ///     User user = getCurrentUser(authentication);
       ///     Order order = checkoutService.checkout(user.getId(), request);
       ///     return "redirect:/orders/" + order.getId();

       /// } catch (Exception e) {
       ///     redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       ///     return "redirect:/checkout";
       /// }
   /// }
       @PostMapping("/checkout")
       public String placeOrder(@ModelAttribute("checkoutRequest") CheckoutRequest request,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

           try {
               User user = getCurrentUser(authentication);
               Order order = checkoutService.checkout(user.getId(), request);

               if (order.getPaymentMethod() == Order.PaymentMethod.BANK_TRANSFER) {
                   return "redirect:/payment/qr/" + order.getId();
               }

               return "redirect:/orders/" + order.getId();

           } catch (Exception e) {
               redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
               return "redirect:/checkout";
           }
       }
}