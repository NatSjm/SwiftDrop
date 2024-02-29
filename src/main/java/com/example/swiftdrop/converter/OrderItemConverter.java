package com.example.swiftdrop.converter;
import com.example.swiftdrop.dto.OrderDtoItem;
import com.example.swiftdrop.entity.OrderItem;


public class OrderItemConverter {
    public static OrderDtoItem toOrderDtoItem(OrderItem orderItem){
      return OrderDtoItem.builder()
              .productId(orderItem.getProduct().getId())
              .productName(orderItem.getProduct().getName())
              .productPrice(orderItem.getProduct().getPrice())
              .quantity(orderItem.getQuantity())
              .build();
    }

}
