package com.example.swiftdrop.model;

import java.util.List;

public interface OrderRequest {
    Long getCustomerId();
    List<OrderItem> getOrderItems();
}