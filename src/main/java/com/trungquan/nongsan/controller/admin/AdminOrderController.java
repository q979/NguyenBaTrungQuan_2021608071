package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.dto.OrderDetailDTO;
import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders_management")
@AllArgsConstructor
public class AdminOrderController extends BaseController {

    private final OrderService orderService;

    @GetMapping
    @Transactional(readOnly = true)
    public String getAllOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        int pageSize = 5;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        Page<Order> orderPage = orderService.searchOrders(status, startDate, endDate, pageable);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());

        return "admin/order";
    }

    @GetMapping("/details/{id}")
    @Transactional(readOnly = true)
    public String details(Model model, @PathVariable Long id) {
        OrderDetailDTO dto = orderService.getOrderDetailDTO(id);
        model.addAttribute("orderDetail", dto);
        return "admin/order_detail";
    }

    @GetMapping("/details/process/{id}")
    @Transactional
    public String process(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        orderService.setProcessingOrder(order);
        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/deliver/{id}")
    @Transactional
    public String deliver(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        orderService.setDeliveringOrder(order);
        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/delivered/{id}")
    @Transactional
    public String delivered(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        orderService.setReceivedToOrder(order);
        return "redirect:/admin/orders_management/details/" + id;
    }

    @GetMapping("/details/cancel/{id}")
    @Transactional
    public String cancel(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        orderService.cancelOrder(order);
        return "redirect:/admin/orders_management/details/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders_management";
    }
}
