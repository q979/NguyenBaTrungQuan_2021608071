package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.dto.CartDTO;
import com.trungquan.nongsan.dto.CartItemDTO;
import com.trungquan.nongsan.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Override
    public void addToCart(HttpSession session, CartItemDTO cartItem) {
        CartDTO cart = getCart(session);
        List<CartItemDTO> cartItems = cart.getCartItems();

        // Kiểm tra xem mục đã tồn tại trong giỏ hàng chưa
        Optional<CartItemDTO> existingCartItem = cartItems.stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Cập nhật số lượng nếu mục đã tồn tại
            CartItemDTO existingItem = existingCartItem.get();
            int stockQty = existingItem.getStockQty() != null ? existingItem.getStockQty() : 0;
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            // Giới hạn không vượt quá số lượng tồn kho
            existingItem.setQuantity(Math.min(newQuantity, stockQty));
        } else {
            // Giới hạn số lượng thêm mới không vượt quá tồn kho
            int stockQty = cartItem.getStockQty() != null ? cartItem.getStockQty() : 0;
            cartItem.setQuantity(Math.min(cartItem.getQuantity(), stockQty));
            // Thêm mới mục vào giỏ hàng
            cartItems.add(cartItem);
        }

        session.setAttribute("cart", cart);
    }


    @Override
    public void updateCartItemQuantity(HttpSession session, Long productId, int quantity) {
        CartDTO cart = getCart(session);
        List<CartItemDTO> cartItems = cart.getCartItems();

        // Tìm mục cần cập nhật
        Optional<CartItemDTO> cartItemToUpdate = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        cartItemToUpdate.ifPresent(item -> {
            int stockQty = item.getStockQty() != null ? item.getStockQty() : 0;
            // Giới hạn không vượt quá số lượng tồn kho
            item.setQuantity(Math.min(quantity, stockQty));
        });

        session.setAttribute("cart", cart);
    }


    @Override
    public void removeCartItem(HttpSession session, Long productId) {
        CartDTO cart = getCart(session);
        List<CartItemDTO> cartItems = cart.getCartItems();

        cartItems.removeIf(item -> item.getProductId().equals(productId));

        session.setAttribute("cart", cart);
    }


    @Override
    public void clearCart(HttpSession session) {
        CartDTO cart = new CartDTO();
        session.setAttribute("cart", cart);
    }

    @Override
    public CartDTO getCart(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute("cart");
        if (cart == null) {
            cart = new CartDTO();
            session.setAttribute("cart", cart);
        }
        return cart;
    }


}
