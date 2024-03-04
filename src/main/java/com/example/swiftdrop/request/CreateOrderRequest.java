package com.example.swiftdrop.request;
import lombok.*;


import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest implements OrderRequest {
        private Long customerId;
        private List<OrderItemRequest> orderItems;
}
