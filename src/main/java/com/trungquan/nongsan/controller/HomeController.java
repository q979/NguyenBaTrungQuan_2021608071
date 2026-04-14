package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.entity.Promotion;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.CategoryService;
import com.trungquan.nongsan.service.PromotionService;
import com.trungquan.nongsan.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@AllArgsConstructor
@Controller

public class HomeController extends BaseController {

    private ProductService productService;
    private CategoryService categoryService;
    private RecommendationService recommendationService;
    private PromotionService promotionService;

    @GetMapping("/")
    String getUserHomePage(Model model) {

        List<Product> top4BestSeller = productService.getTop4BestSeller();
        List<Promotion> promotionList = promotionService.getActivePromotionList();
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("top4BestSeller", top4BestSeller);
        List<Product> newProducts = productService.findAllOrderByCreatedDate();
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("promotionList", promotionList);

        User currentUser = super.getCurrentUser();
        if (currentUser != null) {
            List<Product> recommendations = recommendationService.getRecommendationsForUser(currentUser.getId(), 8);
            model.addAttribute("recommendedProducts", recommendations);
        } else {
            List<Product> recommendations = productService.findAllOrderByCreatedDate();
            model.addAttribute("recommendedProducts", recommendations);
        }

        return "user/index";
    }


}
