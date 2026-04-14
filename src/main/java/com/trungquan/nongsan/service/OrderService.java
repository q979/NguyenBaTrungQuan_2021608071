package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.dto.CartDTO;
import com.trungquan.nongsan.dto.OrderDetailDTO;
import com.trungquan.nongsan.dto.OrderPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    List<Order> getAllOrdersByUser(User user);

    Order getOrderById(Long orderId);

    OrderDetailDTO getOrderDetailDTO(Long orderId);

    Order createUnpaidOrder(CartDTO cart, User user, OrderPerson orderPerson);

    Order createOrder(CartDTO cart, User user, OrderPerson orderPerson, String paymentMethod);

    Order updateOrder(Order order);

    void deleteOrder(Long orderId);

    void cancelOrder(Order order);

    Page<Order> getOrdersByStatus(String status, Pageable pageable);

    Page<Order> getAllOrdersOnPage(Pageable pageable);

    void setProcessingOrder(Order order);

    void setDeliveringOrder(Order order);

    void setReceivedToOrder(Order order);

    List<Order> getTop10orders();

    BigDecimal getTotalRevenue();

    Long countOrder();

    void updateUnpaidToPending(Order order);

    Order createOrderQR(CartDTO cart, User user, OrderPerson orderPerson, String orderCode);

    Page<Order> searchOrders(String status, String startDate, String endDate, Pageable pageable);
}
