package com.example.swiftdrop.service;

import com.example.swiftdrop.entity.Order;
import com.example.swiftdrop.entity.OrderItem;
import com.example.swiftdrop.entity.Product;
import com.example.swiftdrop.repository.OrderItemRepository;
import com.example.swiftdrop.repository.ProductRepository;
import com.example.swiftdrop.request.OrderItemRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Getter
public class OrderItemService {
    private OrderItemRepository orderItemRepository;
    private ProductRepository productRepository;


    public List<OrderItem> saveAll(List<OrderItemRequest> orderItemsRequest, Order order) {
        List<OrderItem> orderItems =  Optional.ofNullable(orderItemsRequest)
                .map(requestItems -> requestItems.stream()
                        .map(p -> mapToOrderItem(p, order)).toList())
                .orElse(List.of());
        return (List<OrderItem>) orderItemRepository.saveAll(orderItems);
    }


    public OrderItem mapToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        Product product = productRepository.findById(orderItemRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + orderItemRequest.getProductId()));

       return  OrderItem.builder().order(order).product(product).quantity(orderItemRequest.getQuantity()).build();
    }

    public OrderItem save(OrderItemRequest orderItemRequest, Order order) {
        OrderItem orderItem = mapToOrderItem(orderItemRequest, order);
        return this.orderItemRepository.save(orderItem);
    }
}
