package com.trungquan.nongsan.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Tag(name = "Get revenue controller", description = "Operations related to product's management")
public interface IProductResource {

    @Operation(summary = "Get product by id", description = "Get product's information based on the given id in path variable")
    @GetMapping("/products/get/{productId}")
    ResponseEntity<?> getProductById(@PathVariable("productId") Long productId);

    @Operation(summary = "Get paginated product list by  category id and sorting key", description = "Get paginated product list's information by  category id and sorting key")
    @GetMapping("/products/get")
    ResponseEntity<?> getProductListPaginatedAndSorted(@RequestParam("sortBy") String sortBy,
                                                    @RequestParam("categoryId") Long categoryId,
                                                    @RequestParam(name = "page", defaultValue = "1") int page,
                                                    @RequestParam("size") int size);

}
