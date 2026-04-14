package com.trungquan.nongsan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends AbstractBase {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "reciever")
    private String reciever;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "vnp_TxnRef")
    private String vnpTxnRef;

    @Column(name = "code", nullable = true)
    private String code;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @Transient
    public String getTotalPriceFormatted() {
        if (totalPrice == null) return "0";
        return new DecimalFormat("#,###").format(totalPrice);
    }


    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetail.setOrder(this);
        orderDetails.add(orderDetail);
    }

    public void removeOrderDetail(OrderDetail orderDetail) {
        orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
    }
}


