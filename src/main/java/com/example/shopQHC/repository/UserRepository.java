package com.example.shopQHC.repository;

import com.example.shopQHC.entity.User;
import com.example.shopQHC.entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    // ================== THỐNG KÊ ==================

    // 1. Tổng số khách hàng
    long countByRole(Role role);

    // 2. Top khách hàng theo số đơn hàng
    @Query(value = """
        SELECT u.full_name, COUNT(o.id) AS total_orders
        FROM orders o
        JOIN users u ON o.user_id = u.id
        GROUP BY u.id, u.full_name
        ORDER BY total_orders DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTopCustomersByOrderCount();

    // 3. Top khách hàng theo chi tiêu
    @Query(value = """
        SELECT u.full_name, COALESCE(SUM(o.final_amount), 0) AS total_spent
        FROM orders o
        JOIN users u ON o.user_id = u.id
        WHERE o.order_status IN ('CONFIRMED', 'SHIPPING', 'COMPLETED')
        GROUP BY u.id, u.full_name
        ORDER BY total_spent DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTopCustomersBySpending();
}