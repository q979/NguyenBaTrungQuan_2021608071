package com.trungquan.nongsan.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.UnsupportedEncodingException;

@Validated
@Tag(name = "Get Revenue Controller", description = "Operations related to get products, categories revenue")
public interface IGetRevenueController {

    @Operation(summary = "Products Revenue", description = "Find top 10 product's revenue by month")
    @GetMapping("/get-revenue/products-by-month/{selectedMonth}")
    ResponseEntity<?> getProductRevenueByMonth(@PathVariable("selectedMonth") int selectedMonth) throws UnsupportedEncodingException;

    @Operation(summary = "Categories Revenue", description = "Find top 10 category's revenue by month")
    @GetMapping("/get-revenue/monthly-revenue-by-year/{selectedYear}")
    ResponseEntity<?> getMonthRevenueByYear(@PathVariable("selectedYear") int selectedYear) throws UnsupportedEncodingException;

}
