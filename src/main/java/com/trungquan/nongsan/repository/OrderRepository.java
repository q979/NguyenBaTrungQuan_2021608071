package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Page<Order> findByStatus(String status, Pageable pageable);

    List<Order> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT SUM(o.totalPrice) FROM Order o where  o.status = 'DELIVERED'")
    BigDecimal sumTotalPrice();

    Optional<Order> findByVnpTxnRef(String vnpTxnRef);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end ORDER BY o.createdAt DESC")
    Page<Order> findByCreatedAtBetween(java.util.Date start, java.util.Date end, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt >= :start AND o.createdAt <= :end ORDER BY o.createdAt DESC")
    Page<Order> findByStatusAndCreatedAtBetween(String status, java.util.Date start, java.util.Date end, Pageable pageable);
}
