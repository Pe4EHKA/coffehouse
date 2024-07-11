package ru.ufanet.test.coffehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer clientId;
    private Integer employeeId;
    private LocalDateTime expectedDeliveryTime;
    private Integer productId;
    private BigDecimal productCost;
    private LocalDateTime createdAt;
    private boolean isCompleted;
    @Transient
    private String status;  // Статус заказа определяется на основе последовательности событий, связанных с заказом
    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL)
    private List<OrderEvent> events;
}
