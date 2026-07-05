package com.tanduydev.ecommerce.model;

import com.tanduydev.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Order extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(unique = true)
    private String orderCode;

    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String receiverName;
    private String receiverPhone;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;
@Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String paymentStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}