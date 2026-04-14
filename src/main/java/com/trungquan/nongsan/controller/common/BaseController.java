package com.trungquan.nongsan.controller.common;

import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.dto.CartDTO;
import com.trungquan.nongsan.service.CartService;
import com.trungquan.nongsan.service.VietQRService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public abstract class BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private VietQRService vietQRService;

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpSession session) {
        CartDTO cart = cartService.getCart(session);
        return cart.getCartItems().size();
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @ModelAttribute("showChatWidget")
    public boolean showChatWidget() {
        return getCurrentUser() == null;
    }

}