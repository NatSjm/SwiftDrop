package com.example.swiftdrop.request;

import java.util.List;

public interface OrderRequest {
    Long getCustomerId();
    List<OrderItemRequest> getOrderItems();
}