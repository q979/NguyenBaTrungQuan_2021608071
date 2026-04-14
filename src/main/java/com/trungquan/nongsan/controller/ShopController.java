package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.dto.UserSearchDTO;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.entity.Category;
import com.trungquan.nongsan.service.CategoryService;
import com.trungquan.nongsan.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/shop")
public class ShopController extends BaseController {

    private CategoryService categoryService;
    private ProductService productService;
    private RecommendationService recommendationService;

    @GetMapping
    public String getShopPage(
            @ModelAttribute("searchModel") UserSearchDTO searchModel,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        Pageable pageable = PageRequest.of(page - 1, 6);

        Page<Product> searchResult;

        if (searchModel.isEmpty()) {
            searchResult = productService.getAllProductsForUsers(pageable);
        } else {
            searchResult = productService.searchProductsUser(searchModel, pageable);
        }

        model.addAttribute("products", searchResult);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("currentPage", searchResult.getNumber());
        model.addAttribute("sortBy", searchModel.getSortBy());
        model.addAttribute("categoryId", searchModel.getCategoryId());
        model.addAttribute("amountGap", searchModel.getAmountGap());
        model.addAttribute("totalProducts", searchResult.getTotalElements());

        if (searchModel.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(searchModel.getCategoryId());
            model.addAttribute("categoryName", category.getName());
        } else {
            model.addAttribute("categoryName", "Tất cả sản phẩm");
        }

        return "user/shop";
    }

    @GetMapping("/product/{product_id}")
    public String viewProductDetail(@PathVariable Long product_id, Model model) {
        Product product = productService.getProductById(product_id);
        model.addAttribute("product", product);
        List<Product> similarProducts = recommendationService.getSimilarProducts(product_id, 4);
        model.addAttribute("similarProducts", similarProducts);
        return "user/product_details";
    }
}
