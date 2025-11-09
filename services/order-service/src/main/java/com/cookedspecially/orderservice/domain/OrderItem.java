package com.cookedspecially.orderservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Item Entity
 *
 * Represents an individual item within an order.
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_menu_item_id", columnList = "menuItemId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long menuItemId;

    @Column(nullable = false, length = 255)
    private String menuItemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(columnDefinition = "TEXT")
    private String specialInstructions;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper method
    public void calculateTotalPrice() {
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
