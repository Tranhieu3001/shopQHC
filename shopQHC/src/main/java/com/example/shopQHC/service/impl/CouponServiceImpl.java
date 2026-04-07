package com.example.shopQHC.service.impl;

import com.example.shopQHC.dto.response.CouponApplyResponse;
import com.example.shopQHC.entity.Coupon;
import com.example.shopQHC.entity.Coupon.Status;
import com.example.shopQHC.repository.CouponRepository;
import com.example.shopQHC.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public List<Coupon> getAllCoupons(String keyword, Status status) {
        if (!StringUtils.hasText(keyword)) {
            keyword = null;
        }
        return couponRepository.searchCoupons(keyword, status);
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy mã giảm giá với id = " + id));
    }

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        normalizeCoupon(coupon);

        if (couponRepository.existsByCodeIgnoreCase(coupon.getCode())) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
        }

        validateCoupon(coupon);
        updateExpiredStatus(coupon);

        return couponRepository.save(coupon);
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon coupon) {
        Coupon existing = getCouponById(id);

        normalizeCoupon(coupon);

        if (!existing.getCode().equalsIgnoreCase(coupon.getCode())
                && couponRepository.existsByCodeIgnoreCase(coupon.getCode())) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
        }

        validateCoupon(coupon);

        existing.setCode(coupon.getCode());
        existing.setDiscountType(coupon.getDiscountType());
        existing.setDiscountValue(coupon.getDiscountValue());
        existing.setMinOrderValue(coupon.getMinOrderValue());
        existing.setStartDate(coupon.getStartDate());
        existing.setEndDate(coupon.getEndDate());
        existing.setQuantity(coupon.getQuantity());
        existing.setStatus(coupon.getStatus());

        updateExpiredStatus(existing);

        return couponRepository.save(existing);
    }

    @Override
    public void updateCouponStatus(Long id, Status status) {
        Coupon coupon = getCouponById(id);
        coupon.setStatus(status);
        updateExpiredStatus(coupon);
        couponRepository.save(coupon);
    }

    @Override
    public CouponApplyResponse applyCoupon(String code, BigDecimal totalAmount) {
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        if (!StringUtils.hasText(code)) {
            return new CouponApplyResponse(
                    false,
                    "Vui lòng nhập mã giảm giá",
                    totalAmount,
                    BigDecimal.ZERO,
                    totalAmount
            );
        }

        String normalizedCode = code.trim().toUpperCase();

        Coupon coupon = couponRepository
                .findByCodeIgnoreCaseAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        normalizedCode,
                        Coupon.Status.ACTIVE,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
                .orElse(null);

        if (coupon == null) {
            return new CouponApplyResponse(
                    false,
                    "Mã giảm giá không hợp lệ hoặc đã hết hạn",
                    totalAmount,
                    BigDecimal.ZERO,
                    totalAmount
            );
        }

        if (coupon.getQuantity() == null || coupon.getQuantity() <= 0) {
            return new CouponApplyResponse(
                    false,
                    "Mã giảm giá đã hết lượt sử dụng",
                    totalAmount,
                    BigDecimal.ZERO,
                    totalAmount
            );
        }

        if (coupon.getMinOrderValue() != null && totalAmount.compareTo(coupon.getMinOrderValue()) < 0) {
            return new CouponApplyResponse(
                    false,
                    "Đơn hàng chưa đạt giá trị tối thiểu để dùng mã",
                    totalAmount,
                    BigDecimal.ZERO,
                    totalAmount
            );
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
            discountAmount = totalAmount
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discountAmount = coupon.getDiscountValue();
        }

        if (discountAmount.compareTo(totalAmount) > 0) {
            discountAmount = totalAmount;
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        return new CouponApplyResponse(
                true,
                "Áp dụng mã thành công",
                totalAmount,
                discountAmount,
                finalAmount
        );
    }

    private void validateCoupon(Coupon coupon) {
        if (!StringUtils.hasText(coupon.getCode())) {
            throw new IllegalArgumentException("Vui lòng nhập mã giảm giá");
        }

        if (coupon.getDiscountType() == null) {
            throw new IllegalArgumentException("Vui lòng chọn loại giảm giá");
        }

        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0");
        }

        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT
                && coupon.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Giảm theo phần trăm không được vượt quá 100%");
        }

        if (coupon.getMinOrderValue() != null && coupon.getMinOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá trị đơn hàng tối thiểu không được âm");
        }

        if (coupon.getStartDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu");
        }

        if (coupon.getEndDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày kết thúc");
        }

        if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (coupon.getQuantity() == null || coupon.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
    }

    private void updateExpiredStatus(Coupon coupon) {
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(LocalDateTime.now())) {
            coupon.setStatus(Status.EXPIRED);
            return;
        }

        if (coupon.getQuantity() != null && coupon.getQuantity() <= 0) {
            coupon.setStatus(Status.INACTIVE);
            return;
        }

        if (coupon.getStatus() == null || coupon.getStatus() == Status.EXPIRED) {
            coupon.setStatus(Status.ACTIVE);
        }
    }

    private void normalizeCoupon(Coupon coupon) {
        if (coupon.getCode() != null) {
            coupon.setCode(coupon.getCode().trim().toUpperCase());
        }
    }
}