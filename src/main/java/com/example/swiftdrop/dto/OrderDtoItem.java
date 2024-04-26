package com.example.swiftdrop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDtoItem {
    private Long productId;
    private int quantity;
    private String productName;
    private Double productPrice;
}
