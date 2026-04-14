package com.trungquan.nongsan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDetailDTO {
    private Long orderId;
    private String code;
    private String receiver;
    private String phoneNumber;
    private String emailAddress;
    private String shippingAddress;
    private String createdAt;
    private String totalPriceFormatted;
    private Double totalPrice;
    private String status;
    private String paymentMethod;
    private List<OrderDetailItemDTO> items;
}
