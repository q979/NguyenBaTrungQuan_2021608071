package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping("/wishlist")
public class WishListController extends BaseController {

    private final UserService userService;
    private final ProductService productService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addToWishList(@RequestParam Long productId) {
        User currentUser = getCurrentUser();
        Product product = productService.getProductById(productId);

        if (product != null) {
            userService.addProductToUser(currentUser.getId(), productId);
            return ResponseEntity.ok("ok");
        }

        return ResponseEntity.badRequest().body("Product not found");
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<String> removeFromWishList(@RequestParam Long productId) {
        User currentUser = getCurrentUser();
        Product product = productService.getProductById(productId);

        if (product != null) {
            userService.removeProductFromUser(currentUser.getId(), productId);
            return ResponseEntity.ok("ok");
        }

        return ResponseEntity.badRequest().body("Product not found");
    }

    @GetMapping
    public String getWishList(Model model) {
        Set<Product> favoritesList = productService.getFavoriteProductsByUserId(getCurrentUser().getId());
        model.addAttribute("favoritesList", favoritesList);
        return "user/wishlist";
    }


}
