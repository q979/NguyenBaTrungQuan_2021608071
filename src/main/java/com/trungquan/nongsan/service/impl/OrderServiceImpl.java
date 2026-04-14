package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.constant.OrderStatus;
import com.trungquan.nongsan.constant.PaymentMethod;
import com.trungquan.nongsan.dto.CartItemDTO;
import com.trungquan.nongsan.dto.OrderDetailDTO;
import com.trungquan.nongsan.dto.OrderDetailItemDTO;
import com.trungquan.nongsan.dto.OrderPerson;
import com.trungquan.nongsan.entity.Product;
import com.trungquan.nongsan.entity.Order;
import com.trungquan.nongsan.entity.OrderDetail;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.repository.ProductRepository;
import com.trungquan.nongsan.repository.OrderDetailRepository;
import com.trungquan.nongsan.repository.OrderRepository;
import com.trungquan.nongsan.service.OrderService;
import com.trungquan.nongsan.dto.CartDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getAllOrdersOnPage(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void setProcessingOrder(Order order) {
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void setDeliveringOrder(Order order) {
        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void setReceivedToOrder(Order order) {
        // Reload order để đảm bảo orderDetails được load trong transaction này
        Order existingOrder = orderRepository.findById(order.getId()).orElse(order);
        existingOrder.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(existingOrder);
        // Cộng doanh thu và buyCount khi đơn hoàn thành
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrder(existingOrder);
        for (OrderDetail orderDetail : orderDetailList) {
            Product product = orderDetail.getProduct();
            if (product != null) {
                int currentBuyCount = product.getBuyCount() != null ? product.getBuyCount() : 0;
                product.setBuyCount(currentBuyCount + orderDetail.getQuantity());
                double currentRevenue = product.getTotalRevenue() != null ? product.getTotalRevenue() : 0.0;
                product.setTotalRevenue(currentRevenue + orderDetail.getPrice());
                productRepository.save(product);
            }
        }
    }

    @Override
    public List<Order> getTop10orders() {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.sumTotalPrice();
    }

    @Override
    public Long countOrder() {
        return orderRepository.count();
    }

    @Override
    public List<Order> getAllOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDTO getOrderDetailDTO(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

        DecimalFormat df = new DecimalFormat("#,###");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        List<OrderDetailItemDTO> itemDTOs = new ArrayList<>();
        for (OrderDetail detail : orderDetails) {
            String categoryName = detail.getProduct().getCategory() != null
                    ? detail.getProduct().getCategory().getName() : "—";
            String origPriceFmt = detail.getProduct().getOriginalPrice() != null
                    ? df.format(detail.getProduct().getOriginalPrice()) : "0";
            String salePriceFmt = detail.getProduct().getSalePrice() != null
                    ? df.format(detail.getProduct().getSalePrice()) : "0";
            double totalPrice = (detail.getQuantity() != null ? detail.getQuantity() : 0)
                    * (detail.getPrice() != null ? detail.getPrice() : 0.0);
            String totalPriceFmt = df.format(totalPrice);

            itemDTOs.add(new OrderDetailItemDTO(
                    detail.getProduct().getId(),
                    detail.getProduct().getTitle(),
                    detail.getProduct().getProducer(),
                    categoryName,
                    origPriceFmt,
                    salePriceFmt,
                    detail.getQuantity(),
                    detail.getPrice(),
                    totalPriceFmt
            ));
        }

        String createdAtFmt = order.getCreatedAt() != null
                ? dateFormat.format(order.getCreatedAt()) : "";
        String totalPriceFmt = order.getTotalPrice() != null
                ? df.format(order.getTotalPrice()) : "0";

        return new OrderDetailDTO(
                order.getId(),
                order.getCode(),
                order.getReciever(),
                order.getPhoneNumber(),
                order.getEmailAddress(),
                order.getShippingAddress(),
                createdAtFmt,
                totalPriceFmt,
                order.getTotalPrice(),
                order.getStatus(),
                order.getPaymentMethod(),
                itemDTOs
        );
    }

    @Override
    public Order createUnpaidOrder(CartDTO cart, User user, OrderPerson orderPerson) {
        Order order = new Order();
        order.setReciever(orderPerson.getFullName());
        order.setStatus(OrderStatus.UNPAID);
        order.setEmailAddress(orderPerson.getEmail());
        order.setShippingAddress(orderPerson.getAddress());
        order.setPhoneNumber(orderPerson.getPhoneNumber());
        order.setTotalPrice(cart.calculateTotalAmount());
        order.setPaymentMethod(PaymentMethod.COD);

        // Thêm các chi tiết đơn hàng từ giỏ hàng
        List<CartItemDTO> cartItems = cart.getCartItems();
        for (CartItemDTO cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            assert product != null;
            orderDetail.setPrice(product.getSalePrice());
            order.addOrderDetail(orderDetail);
            double currentTotalRevenue = product.getTotalRevenue();
            if (product.getTotalRevenue() == 0) {
                currentTotalRevenue = (double) 0;
            }
            product.setTotalRevenue(currentTotalRevenue + (double) orderDetail.getPrice());
        }

        // Set thông tin người dùng và thời gian
        order.setUser(user);
        order.setCreatedAt(new Date());
        // Lưu đơn đặt hàng vào cơ sở dữ liệu
        return orderRepository.save(order);
    }

    public static String generateOrderCode() {
        int codeLength = 10;

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        String prefix = "PRODUCT";

        return prefix + "-" + code.toString();
    }
    @Override
    public Order createOrder(CartDTO cart, User user, OrderPerson orderPerson, String paymentMethod) {
        Order order = new Order();
        order.setReciever(orderPerson.getFullName());
        order.setStatus(OrderStatus.PENDING);
        order.setEmailAddress(orderPerson.getEmail());
        order.setShippingAddress(orderPerson.getAddress());
        order.setPhoneNumber(orderPerson.getPhoneNumber());
        order.setTotalPrice(cart.calculateTotalAmount());
        order.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : PaymentMethod.COD);
        order.setCode(generateOrderCode());

        // Thêm các chi tiết đơn hàng từ giỏ hàng
        List<CartItemDTO> cartItems = cart.getCartItems();
        for (CartItemDTO cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product == null) {
                continue; // bỏ qua sản phẩm đã bị xóa
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            // Dùng giá từ cartItem (đã tính đúng lúc thêm vào), fallback sang salePrice hoặc originalPrice
            double price = cartItem.getPrice();
            if (price == 0 && product.getOriginalPrice() != null) {
                price = product.getOriginalPrice();
            }
            orderDetail.setPrice(price);
            order.addOrderDetail(orderDetail);

            // Giảm tồn kho khi đặt hàng thành công
            int currentQty = product.getQty() != null ? product.getQty() : 0;
            int orderedQty = cartItem.getQuantity();
            product.setQty(Math.max(0, currentQty - orderedQty));
            productRepository.save(product);
        }

        // Set thông tin người dùng và thời gian
        order.setUser(user);
        order.setCreatedAt(new Date());
        // Lưu đơn đặt hàng vào cơ sở dữ liệu
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createOrderQR(CartDTO cart, User user, OrderPerson orderPerson, String orderCode) {
        Order order = new Order();
        order.setCode(orderCode);
        order.setReciever(orderPerson.getFullName());
        order.setStatus(OrderStatus.PENDING);
        order.setEmailAddress(orderPerson.getEmail());
        order.setShippingAddress(orderPerson.getAddress());
        order.setPhoneNumber(orderPerson.getPhoneNumber());
        order.setTotalPrice(cart.calculateTotalAmount());
        order.setPaymentMethod("QR");

        List<CartItemDTO> cartItems = cart.getCartItems();
        for (CartItemDTO cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product == null) {
                continue;
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getPrice());
            order.addOrderDetail(orderDetail);

            // Giảm tồn kho
            int currentQty = product.getQty() != null ? product.getQty() : 0;
            product.setQty(Math.max(0, currentQty - cartItem.getQuantity()));
            productRepository.save(product);
        }

        order.setUser(user);
        order.setCreatedAt(new Date());

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        // Hoàn trả tồn kho cho các sản phẩm trong đơn hàng bị hủy
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            if (product != null) {
                int currentQty = product.getQty() != null ? product.getQty() : 0;
                product.setQty(currentQty + orderDetail.getQuantity());
                productRepository.save(product);
            }
        }
        orderRepository.save(order);
    }

    @Override
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public void updateUnpaidToPending(Order order) {
        order.setStatus(OrderStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrders(String status, String startDate, String endDate, Pageable pageable) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        if (startDate != null && !startDate.isBlank() && endDate != null && !endDate.isBlank()) {
            try {
                java.util.Date start = sdf.parse(startDate);
                java.util.Date end = sdf.parse(endDate);
                // Set end date to end of day
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(end);
                cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                cal.set(java.util.Calendar.MINUTE, 59);
                cal.set(java.util.Calendar.SECOND, 59);
                end = cal.getTime();

                if (status != null && !status.isBlank()) {
                    return orderRepository.findByStatusAndCreatedAtBetween(status, start, end, pageable);
                } else {
                    return orderRepository.findByCreatedAtBetween(start, end, pageable);
                }
            } catch (java.text.ParseException e) {
                // fallback
            }
        }
        if (status != null && !status.isBlank()) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

}
