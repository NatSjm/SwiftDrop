package com.example.swiftdrop.model;
import com.example.swiftdrop.enums.OrderStatus;
import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;


@Data
public class Order {
    private Long id;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;
    private List<OrderItem> orderItems;
}