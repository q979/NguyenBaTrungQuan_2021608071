package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.OrderDetail;
import com.trungquan.nongsan.entity.composite_key.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product p LEFT JOIN FETCH p.category WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);

    List<OrderDetail> findByOrder(Order order);

    @Query("SELECT od.quantity FROM OrderDetail od WHERE od.order.id = :orderId AND od.product.id = :productId")
    int findByProductAndOrOrder(Long orderId, Long productId);

    List<OrderDetail> findByProductId(long productId);
}
