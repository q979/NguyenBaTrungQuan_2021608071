package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderNotificationService {
    private SimpMessagingTemplate messagingTemplate;

    public void notifyNewOrder(Order order) {
        String message = String.format(
            "{\"orderId\": %d, \"code\": \"%s\", \"receiver\": \"%s\", \"totalPrice\": %.0f, \"createdAt\": \"%s\"}",
            order.getId(),
            order.getCode() != null ? order.getCode() : "",
            order.getReciever(),
            order.getTotalPrice(),
            order.getCreatedAt() != null ? order.getCreatedAt().toString() : ""
        );
        messagingTemplate.convertAndSend("/topic/orders", message);
    }
}