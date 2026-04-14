package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.RecommendationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {

    ProductService productService;
    RecommendationService recommendationService;

    @GetMapping("/sort-products")
    public ModelAndView sortProducts(@RequestParam("sortBy") String sortBy,
                                  @RequestParam("categoryId") Long categoryId,
                                  Model model){
        List<Product> productList;
        if(categoryId != null){
            productList = productService.getAllProductsByCategoryId(categoryId);
        }
        else {
            productList = productService.findAll();
        }

        List<Product> sortedProductList;

        switch (sortBy){
            case "price-low-to-high":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getSalePrice))
                        .collect(Collectors.toList());
                break;
            case "price-high-to-low":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getSalePrice).reversed())
                        .collect(Collectors.toList());
                break;
            case "newest":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getPublishedDate))
                        .collect(Collectors.toList());
                break;
            case "oldest":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getPublishedDate).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                sortedProductList = productList;
        }
        model.addAttribute("productList", sortedProductList);
        return new ModelAndView("fragments/productListFragment :: productList", model.asMap());
    }

}
