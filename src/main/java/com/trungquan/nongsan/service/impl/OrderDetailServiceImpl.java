package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.OrderDetail;
import com.trungquan.nongsan.repository.OrderDetailRepository;
import com.trungquan.nongsan.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> getAllOrderDetailByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> getAllOrderDetailByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }
}
