package com.example.swiftdrop.model;

import lombok.Data;

@Data
public class ExtendedOrderItem {
    private Long productId;
    private int quantity;
    private String productName;
    private Double productPrice;
}
