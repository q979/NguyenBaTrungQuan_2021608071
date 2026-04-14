package com.trungquan.nongsan.controller;

import com.trungquan.nongsan.dto.AddToCartRequest;
import com.trungquan.nongsan.dto.CartUpdateResponse;
import com.trungquan.nongsan.dto.CartItemDTO;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.service.ProductService;
import com.trungquan.nongsan.service.VietQRService;
import com.trungquan.nongsan.dto.CartDTO;
import com.trungquan.nongsan.dto.OrderPerson;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.service.CartService;
import com.trungquan.nongsan.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController extends BaseController {

    private final HttpSession session;
    private final OrderService orderService;
    private CartService cartService;
    private ProductService productService;
    private VietQRService vietQRService;

    @GetMapping
    public String getCartPage(Model model) {
        CartDTO cart = cartService.getCart(session);

        // Cập nhật số tồn kho từ DB cho mỗi item trong giỏ
        for (CartItemDTO item : cart.getCartItems()) {
            Product product = productService.getProductById(item.getProductId());
            if (product != null) {
                item.setStockQty(product.getQty());
            }
        }

        model.addAttribute("cart", cart);
        double totalCart = cart.calculateTotalAmount();
        model.addAttribute("totalCart", totalCart);
        return "user/cart";
    }

    @PostMapping("/add-to-cart")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {

        if (getCurrentUser() != null) {
            Long productId = request.getProductId();
            Integer quantity = request.getQuantity();
            Product existingProduct = productService.getProductById(productId);

            if (existingProduct.getQty() == null || existingProduct.getQty() == 0) {
                return ResponseEntity.ok(new CartUpdateResponse(false, 0, 0, 0, 0, "Sản phẩm đã hết hàng"));
            }

            // Tính số lượng hợp lệ: không vượt quá stock
            int stockQty = existingProduct.getQty();
            int validQty = Math.min(quantity, stockQty);

            CartItemDTO addedItem = new CartItemDTO();
            addedItem.setQuantity(validQty);
            addedItem.setProductId(productId);
            addedItem.setTitle(existingProduct.getTitle());
            if (existingProduct.getSalePrice() == null || existingProduct.getSalePrice() <= 0) {
                addedItem.setPrice(existingProduct.getOriginalPrice());
            } else {
                addedItem.setPrice(existingProduct.getSalePrice());
            }
            addedItem.setCoverImage(existingProduct.getCoverImage());
            addedItem.setStockQty(stockQty);
            cartService.addToCart(session, addedItem);

            CartDTO cart = cartService.getCart(session);
            CartItemDTO item = cart.getCartItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst().orElse(null);

            int cartQty = item != null ? item.getQuantity() : 0;
            double itemSubtotal = item != null ? item.getSubtotal() : 0.0;
            String msg = "Thêm vào giỏ hàng thành công";
            if (cartQty >= stockQty) {
                msg = "Đã đạt số lượng tối đa trong kho";
            }

            return ResponseEntity.ok(new CartUpdateResponse(
                    true, itemSubtotal, cart.calculateTotalAmount(),
                    cart.getCartItems().size(), stockQty, msg));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @PostMapping("/update-cart-item")
    @ResponseBody
    public ResponseEntity<CartUpdateResponse> updateCartItem(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.updateCartItemQuantity(session, productId, quantity);
        CartDTO cart = cartService.getCart(session);

        // Lấy thông tin item vừa update
        CartItemDTO updatedItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        double itemSubtotal = updatedItem != null ? updatedItem.getSubtotal() : 0.0;
        int maxQty = updatedItem != null && updatedItem.getStockQty() != null ? updatedItem.getStockQty() : 0;

        CartUpdateResponse response = new CartUpdateResponse(
                true,
                itemSubtotal,
                cart.calculateTotalAmount(),
                cart.getCartItems().size(),
                maxQty,
                "Cập nhật thành công"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-cart-item/{id}")
    @ResponseBody
    public ResponseEntity<CartUpdateResponse> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(session, id);
        CartDTO cart = cartService.getCart(session);
        CartUpdateResponse response = new CartUpdateResponse(
                true, 0, cart.calculateTotalAmount(), cart.getCartItems().size(), 0, "Xóa thành công"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart-item-count")
    @ResponseBody
    public int getCartItemCount() {
        return cartService.getCart(session).getCartItems().size();
    }

    @GetMapping("/checkout")
    public String getCheckOut(Model model) {
        CartDTO cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        double totalCart = cart.calculateTotalAmount();
        model.addAttribute("totalCart", totalCart);

        User curUser = getCurrentUser();
        OrderPerson orderPerson = new OrderPerson();
        if (curUser != null) {
            orderPerson.setFullName(curUser.getFullName());
            orderPerson.setEmail(curUser.getEmail());
            orderPerson.setPhoneNumber(curUser.getPhoneNumber());
            orderPerson.setAddress(curUser.getAddress());
        }
        model.addAttribute("orderPerson", orderPerson);

        return "user/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@ModelAttribute("orderPerson") OrderPerson orderPerson,
                            @RequestParam(value = "paymentMethod", required = false, defaultValue = "cod") String paymentMethod) {
        try {
            CartDTO cart = cartService.getCart(session);
            if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
                return "redirect:/cart";
            }
            User curUser = getCurrentUser();
            if (curUser == null) {
                return "redirect:/login";
            }

            // QR chuyển sang trang QR checkout để hiển thị mã thanh toán
            if ("qr".equalsIgnoreCase(paymentMethod)) {
                session.setAttribute("pendingQROrder", true);
                session.setAttribute("pendingOrderPerson", orderPerson);
                return "redirect:/cart/qr-checkout";
            }

            // COD: tạo đơn ngay
            if ("cod".equalsIgnoreCase(paymentMethod)) {
                orderService.createOrder(cart, curUser, orderPerson, paymentMethod);
            }

            cartService.clearCart(session);
            return "redirect:/orders";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart?error=place_order_failed";
        }
    }

    // ====== VietQR Checkout ======

    @GetMapping("/qr-checkout")
    public String getQRCheckoutPage(Model model) {
        CartDTO cart = cartService.getCart(session);
        if (cart == null || cart.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }
        User curUser = getCurrentUser();
        if (curUser == null) {
            return "redirect:/login";
        }

        // Lấy thông tin từ session (nếu có)
        OrderPerson orderPerson = (OrderPerson) session.getAttribute("pendingOrderPerson");
        if (orderPerson == null) {
            orderPerson = new OrderPerson();
        }
        // Pre-fill từ user hiện tại nếu trường trống
        if (curUser != null) {
            if (orderPerson.getFullName() == null || orderPerson.getFullName().isEmpty()) {
                orderPerson.setFullName(curUser.getFullName());
            }
            if (orderPerson.getEmail() == null || orderPerson.getEmail().isEmpty()) {
                orderPerson.setEmail(curUser.getEmail());
            }
            if (orderPerson.getPhoneNumber() == null || orderPerson.getPhoneNumber().isEmpty()) {
                orderPerson.setPhoneNumber(curUser.getPhoneNumber());
            }
            if (orderPerson.getAddress() == null || orderPerson.getAddress().isEmpty()) {
                orderPerson.setAddress(curUser.getAddress());
            }
        }

        // Tạo mã đơn hàng tạm thời (độc nhất)
        String orderCode = "DH" + System.currentTimeMillis();
        session.setAttribute("tempOrderCode", orderCode);

        double total = cart.calculateTotalAmount();

        // Tạo URL QR động
        String vietQRUrl = vietQRService.generateQRUrl(total, orderCode);
        model.addAttribute("vietQRUrl", vietQRUrl);

        // Thông tin tài khoản để hiển thị trong template
        model.addAttribute("accountNumber", vietQRService.getAccountNumber());
        model.addAttribute("accountName", vietQRService.getAccountName());
        model.addAttribute("bankName", "MB Bank");

        model.addAttribute("cart", cart);
        model.addAttribute("totalCart", total);
        model.addAttribute("tempOrderCode", orderCode);
        model.addAttribute("orderPerson", orderPerson);

        return "user/qr_checkout";
    }

    @PostMapping("/place-order-qr")
    public String placeOrderQR() {
        try {
            CartDTO cart = cartService.getCart(session);
            if (cart == null || cart.getCartItems().isEmpty()) {
                return "redirect:/cart";
            }
            User curUser = getCurrentUser();
            if (curUser == null) {
                return "redirect:/login";
            }

            // Lấy thông tin từ session
            OrderPerson orderPerson = (OrderPerson) session.getAttribute("pendingOrderPerson");
            String orderCode = (String) session.getAttribute("tempOrderCode");

            if (orderPerson == null || orderCode == null) {
                return "redirect:/cart";
            }

            // Tạo đơn hàng QR
            orderService.createOrderQR(cart, curUser, orderPerson, orderCode);

            // Xóa session
            session.removeAttribute("cart");
            session.removeAttribute("pendingQROrder");
            session.removeAttribute("pendingOrderPerson");
            session.removeAttribute("tempOrderCode");

            return "redirect:/orders";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart?error=place_order_failed";
        }
    }

}
