package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.dto.ProductDto;
import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.dto.CategoryDto;
import com.trungquan.nongsan.dto.OrderDTO;
import com.trungquan.nongsan.service.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminHomeController extends BaseController {
    private OrderService orderService;
    private UserService userService;
    private ProductService productService;
    private CategoryService categoryService;
    private ExportService exportService;

    @GetMapping
    public String getAdminHomePage(Model model) {
        List<Order> orders = orderService.getTop10orders();
        model.addAttribute("orders", orders);
        BigDecimal totalRevenue = orderService.getTotalRevenue();

        Long numberOfUsers = userService.countUser();
        Long numberOfProducts = productService.countProduct();
        Long numberOfOrders = orderService.countOrder();


        model.addAttribute("numberOfUsers", numberOfUsers);
        model.addAttribute("numberOfProducts", numberOfProducts);
        model.addAttribute("numberOfOrders", numberOfOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/index";
    }

    @ResponseBody
    @GetMapping("/export/order/{keyword}")
	public ResponseEntity<?> generateOrderReport(@PathVariable String keyword) throws FileNotFoundException {
        List<Order> orders = orderService.getTop10orders();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order item : orders){
            OrderDTO orderDTO = new OrderDTO(item.getReciever(), item.getPhoneNumber(), item.getEmailAddress(), item.getCreatedAt().toString(), item.getTotalPrice(), item.getStatus(), item.getPaymentMethod());
            orderDTOList.add(orderDTO);
        }
        return ResponseEntity.ok().body(exportService.exportOrderReport(this.getCurrentUser(), orderDTOList, keyword));
	}

    @ResponseBody
    @GetMapping("/export/category/{selectedMonth}/{keyword}")
    public ResponseEntity<?> generateCategoryReport(@PathVariable int selectedMonth, @PathVariable String keyword) throws FileNotFoundException {
        List<CategoryDto> categoryDtoList = categoryService.getTop10BestSellerByMonth(selectedMonth);
        return ResponseEntity.ok().body(exportService.exportCategoryReport(this.getCurrentUser(),categoryDtoList, keyword));
    }

    @ResponseBody
    @GetMapping("/export/product/{selectedMonth}/{keyword}")
    public ResponseEntity<?> generateProductReport(@PathVariable int selectedMonth, @PathVariable String keyword) throws FileNotFoundException {
        List<ProductDto> productDtoList = productService.getTop10BestSellerByMonth(selectedMonth);
        return ResponseEntity.ok().body(exportService.exportProductReport(this.getCurrentUser(),productDtoList, keyword));
    }
}
