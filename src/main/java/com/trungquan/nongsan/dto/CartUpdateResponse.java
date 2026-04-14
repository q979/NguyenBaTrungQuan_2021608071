package com.trungquan.nongsan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateResponse {
    private boolean success;
    private double itemSubtotal;
    private double cartTotal;
    private int cartItemCount;
    private int maxQuantity; // số lượng tồn kho tối đa
    private String message;

    public CartUpdateResponse() {}

    public CartUpdateResponse(boolean success, double itemSubtotal, double cartTotal, int cartItemCount, String message) {
        this.success = success;
        this.itemSubtotal = itemSubtotal;
        this.cartTotal = cartTotal;
        this.cartItemCount = cartItemCount;
        this.message = message;
    }

    public CartUpdateResponse(boolean success, double itemSubtotal, double cartTotal, int cartItemCount, int maxQuantity, String message) {
        this.success = success;
        this.itemSubtotal = itemSubtotal;
        this.cartTotal = cartTotal;
        this.cartItemCount = cartItemCount;
        this.maxQuantity = maxQuantity;
        this.message = message;
    }
}
