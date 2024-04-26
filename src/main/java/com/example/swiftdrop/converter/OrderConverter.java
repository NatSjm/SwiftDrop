package com.example.swiftdrop.converter;

import com.example.swiftdrop.dto.OrderDto;
import com.example.swiftdrop.dto.OrderDtoItem;
import com.example.swiftdrop.entity.Order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OrderConverter {
    public static OrderDto toOrderDto(Order order) {
        List<OrderDtoItem> OrderDtoItems = Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderItemConverter::toOrderDtoItem)
                .toList();

        return OrderDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .customerId(order.getCustomerId())
                .orderItems(OrderDtoItems)
                .build();
    }
}
