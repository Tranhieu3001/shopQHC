package com.example.shopQHC.repository;

import com.example.shopQHC.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    @Query(value = """
    SELECT p.name, SUM(oi.quantity) AS total_sold
    FROM order_items oi
    JOIN products p ON oi.product_id = p.id
    JOIN orders o ON oi.order_id = o.id
    WHERE o.order_status IN ('CONFIRMED', 'SHIPPING', 'COMPLETED')
    GROUP BY p.id, p.name
    ORDER BY total_sold DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> getTop5BestSellingProducts();
}