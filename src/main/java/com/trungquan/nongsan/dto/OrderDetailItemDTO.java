package com.trungquan.nongsan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDetailItemDTO {
    private Long productId;
    private String productTitle;
    private String producer;
    private String categoryName;
    private String originalPriceFormatted;
    private String salePriceFormatted;
    private Integer quantity;
    private Double price;
    private String totalPriceFormatted;
}
