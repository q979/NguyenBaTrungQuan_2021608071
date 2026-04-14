package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> getAllOrderDetailByOrderId(Long orderId);

    List<OrderDetail> getAllOrderDetailByOrder(Order order);
}
