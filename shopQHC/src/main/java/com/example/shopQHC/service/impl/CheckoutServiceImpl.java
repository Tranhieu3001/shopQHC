package com.example.shopQHC.service.impl;

import com.example.shopQHC.dto.CheckoutRequest;
import com.example.shopQHC.entity.*;
import com.example.shopQHC.repository.*;
import com.example.shopQHC.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    public CheckoutServiceImpl(UserRepository userRepository,
                               CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               CouponRepository couponRepository,
                               OrderRepository orderRepository,
                               OrderItemRepository orderItemRepository,
                               ProductRepository productRepository,
                               ProductSizeRepository productSizeRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productSizeRepository = productSizeRepository;
    }

    @Override
    @Transactional
    public Order checkout(Long userId, CheckoutRequest request) {
        validateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            ProductSize productSize = cartItem.getProductSize();
            Product product = productSize.getProduct();

            if (productSize.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "Sản phẩm " + product.getName() + " - size " + productSize.getSize() + " không đủ tồn kho"
                );
            }

            BigDecimal subTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(subTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productSize(productSize)
                    .productName(product.getName())
                    .size(productSize.getSize())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();

            orderItems.add(orderItem);
        }
        //giam gia
        Coupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (StringUtils.hasText(request.getCouponCode())) {

    String code = request.getCouponCode().trim().toUpperCase();

    appliedCoupon = couponRepository
            .findByCodeIgnoreCaseAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    code,
                    Coupon.Status.ACTIVE,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            )
            .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không hợp lệ hoặc đã hết hạn"));

            if (appliedCoupon.getQuantity() <= 0) {
                throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng");
            }

            if (totalAmount.compareTo(appliedCoupon.getMinOrderValue()) < 0) {
                throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã");
            }

            if (appliedCoupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
                discountAmount = totalAmount
                        .multiply(appliedCoupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                discountAmount = appliedCoupon.getDiscountValue();
            }

            if (discountAmount.compareTo(totalAmount) > 0) {
                discountAmount = totalAmount;
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Order order = Order.builder()
                .user(user)
                .couponId(appliedCoupon != null ? appliedCoupon.getId() : null)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .paymentMethod(request.getPaymentMethod())
                .orderStatus(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        for (CartItem cartItem : cartItems) {
            ProductSize productSize = cartItem.getProductSize();
            Product product = productSize.getProduct();

            productSize.setStockQuantity(productSize.getStockQuantity() - cartItem.getQuantity());
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSoldQuantity(product.getSoldQuantity() + cartItem.getQuantity());

            productSizeRepository.save(productSize);
            productRepository.save(product);
        }

        if (appliedCoupon != null) {
            appliedCoupon.setQuantity(appliedCoupon.getQuantity() - 1);
            if (appliedCoupon.getQuantity() <= 0) {
                appliedCoupon.setStatus(Coupon.Status.INACTIVE);
            }
            couponRepository.save(appliedCoupon);
        }

        cartItemRepository.deleteByCartId(cart.getId());

        return savedOrder;
    }

    private void validateRequest(CheckoutRequest request) {
        if (!StringUtils.hasText(request.getReceiverName())) {
            throw new IllegalArgumentException("Vui lòng nhập tên người nhận");
        }
        if (!StringUtils.hasText(request.getReceiverPhone())) {
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại");
        }
        if (!StringUtils.hasText(request.getShippingAddress())) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ nhận hàng");
        }
        if (request.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán");
        }
    }
}