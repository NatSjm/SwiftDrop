package com.example.swiftdrop.model;

import com.example.swiftdrop.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExtendedOrder {
    private Long id;
    private Long customerId;
    private List<ExtendedOrderItem> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;
}
