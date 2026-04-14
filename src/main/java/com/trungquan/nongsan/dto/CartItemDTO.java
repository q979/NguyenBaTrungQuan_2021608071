package com.trungquan.nongsan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long productId;
    private String coverImage;
    private String title;
    private Double price;
    private Integer quantity;
    private Integer stockQty; // số lượng tồn kho

    public double getSubtotal() {
        return price * quantity;
    }

    // Manual getters for IDE recognition (Lombok will also generate these)
    public Integer getStockQty() {
        return stockQty;
    }
    public Long getProductId() {
        return productId;
    }
    public String getCoverImage() {
        return coverImage;
    }
    public String getTitle() {
        return title;
    }
    public Double getPrice() {
        return price;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }
}
