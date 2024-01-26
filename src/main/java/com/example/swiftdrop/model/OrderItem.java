package com.example.swiftdrop.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderItem {
    private Long productId;
    private int quantity;
    private String productName;
    private Double productPrice;
}