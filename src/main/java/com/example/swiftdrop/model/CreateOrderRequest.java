package com.example.swiftdrop.model;
import lombok.Data;

import java.util.List;
@Data
public class CreateOrderRequest implements OrderRequest {
        private Long customerId;
        private List<OrderItem> orderItems;
}
