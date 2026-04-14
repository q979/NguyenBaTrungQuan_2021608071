package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Custom query methods if needed
    Page<Category> findAll(Pageable pageable);

    @Query(value = "SELECT c.name, SUM(od.price * od.quantity) as total_revenue FROM categories c " +
            "JOIN products b ON c.id = b.category_id " +
            "JOIN order_details od ON b.id = od.product_id " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE MONTH(o.create_date) = :month " +
            "GROUP BY c.name " +
            "ORDER BY total_revenue " +
            "DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10BestSellerByMonth(@Param("month") int month);
}
