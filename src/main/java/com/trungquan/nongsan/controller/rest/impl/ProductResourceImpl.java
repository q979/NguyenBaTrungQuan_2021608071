package com.trungquan.nongsan.controller.rest.impl;

import com.trungquan.nongsan.controller.rest.IProductResource;
import com.trungquan.nongsan.controller.rest.base.RestApiV1;
import com.trungquan.nongsan.controller.rest.base.VsResponseUtil;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductResourceImpl implements IProductResource {

    ProductService productService;

    @Override
    public ResponseEntity<?> getProductById(Long productId) {
        return VsResponseUtil.ok(HttpStatus.OK, productService.getProductById(productId));
    }

    @Override
    public ResponseEntity<?> getProductListPaginatedAndSorted(String sortBy, Long categoryId, int page, int size) {
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
            case "price-high-to-low":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getSalePrice).reversed())
                        .collect(Collectors.toList());
            case "newest":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getPublishedDate))
                        .collect(Collectors.toList());
            case "oldest":
                sortedProductList = productList.stream()
                        .sorted(Comparator.comparing(Product::getPublishedDate).reversed())
                        .collect(Collectors.toList());
            default:
                sortedProductList = productList;
        }
        return VsResponseUtil.ok(HttpStatus.OK, sortedProductList);
    }
}
