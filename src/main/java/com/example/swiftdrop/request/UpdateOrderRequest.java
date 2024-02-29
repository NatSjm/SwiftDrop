package com.example.swiftdrop.request;

import com.example.swiftdrop.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderRequest implements OrderRequest {
    private Long customerId;
    private OrderStatus status;
    private List<OrderItemRequest> orderItems;
}