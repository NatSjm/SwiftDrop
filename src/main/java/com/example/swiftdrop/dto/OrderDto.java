package com.example.swiftdrop.dto;

import com.example.swiftdrop.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private Long customerId;
    private List<OrderDtoItem> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;
}
