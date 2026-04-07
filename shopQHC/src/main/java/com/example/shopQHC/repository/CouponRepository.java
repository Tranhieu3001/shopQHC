package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Coupon;
import com.example.shopQHC.entity.Coupon.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    
    Optional<Coupon> findByCodeIgnoreCase(String code);

    
    Optional<Coupon> findByCodeIgnoreCaseAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String code,
            Coupon.Status status,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    
    boolean existsByCodeIgnoreCase(String code);

    @Query("""
            select c
            from Coupon c
            where (:keyword is null or lower(c.code) like lower(concat('%', :keyword, '%')))
              and (:status is null or c.status = :status)
            order by c.id desc
            """)
    List<Coupon> searchCoupons(String keyword, Status status);
}