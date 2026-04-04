package com.example.shopQHC.repository;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select o
            from Order o
            where (:keyword is null or
                   lower(o.receiverName) like lower(concat('%', :keyword, '%'))
                   or lower(o.receiverPhone) like lower(concat('%', :keyword, '%')))
              and (:orderStatus is null or o.orderStatus = :orderStatus)
            order by o.createdAt desc
            """)
    List<Order> searchOrders(String keyword, OrderStatus orderStatus);

    @Query("""
            select distinct o
            from Order o
            left join fetch o.user
            left join fetch o.orderItems oi
            left join fetch oi.product
            where o.id = :id
            """)
    Optional<Order> findWithDetailsById(Long id);
    @EntityGraph(attributePaths = {"user"})
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    // thong ke
    @Query("""
    SELECT COALESCE(SUM(o.finalAmount), 0)
    FROM Order o
    WHERE o.orderStatus IN :statuses
    """)
    BigDecimal sumRevenueByStatuses(@Param("statuses") List<Order.OrderStatus> statuses);
    // doanh thu theo thang
    @Query(value = """
    SELECT MONTH(o.order_date) AS month, SUM(o.final_amount) AS revenue
    FROM orders o
    WHERE YEAR(o.order_date) = :year
      AND o.order_status IN ('CONFIRMED', 'SHIPPING', 'COMPLETED')
    GROUP BY MONTH(o.order_date)
    ORDER BY MONTH(o.order_date)
    """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("year") int year);
}