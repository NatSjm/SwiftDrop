package com.example.swiftdrop.service;

import com.example.swiftdrop.converter.OrderConverter;
import com.example.swiftdrop.entity.Order;
import com.example.swiftdrop.entity.OrderItem;
import com.example.swiftdrop.entity.ProductOrderId;
import com.example.swiftdrop.enums.OrderStatus;
import com.example.swiftdrop.dto.*;
import com.example.swiftdrop.repository.OrderItemRepository;
import com.example.swiftdrop.repository.OrderRepository;
import com.example.swiftdrop.repository.ProductRepository;
import com.example.swiftdrop.request.CreateOrderRequest;
import com.example.swiftdrop.request.OrderItemRequest;
import com.example.swiftdrop.request.UpdateOrderRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import com.example.swiftdrop.exception.OrderNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Getter
public class OrderService {
    private final Map<Long, Order> orders = new HashMap<>();
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private ProductRepository productRepository;
    private OrderItemService orderItemService;

    @Transactional
    public OrderDto save(CreateOrderRequest orderRequest) {
        Order order = Order.builder().customerId(orderRequest.getCustomerId()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).status(OrderStatus.NEW).build();
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = orderItemService.saveAll(orderRequest.getOrderItems(), savedOrder);
        savedOrder.setOrderItems(orderItems);
        return OrderConverter.toOrderDto(savedOrder);
    }

    public OrderDto getOrder(Long orderId) {
        return orderRepository.
                findById(orderId).map(OrderConverter::toOrderDto)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    public OrderDto update(UpdateOrderRequest orderRequest, Long orderId) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        oldOrder.setUpdatedAt(LocalDateTime.now());
        oldOrder.setCustomerId(orderRequest.getCustomerId());
        oldOrder.setStatus(orderRequest.getStatus());

        List<OrderItem> existingOrderItems = oldOrder.getOrderItems();

        Map<Long, OrderItem> existingOrderItemMap = existingOrderItems.stream()
                .collect(Collectors.toMap(orderItem -> orderItem.getProduct().getId(), orderItem -> orderItem));

        List<OrderItem> updatedOrderItems = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : orderRequest.getOrderItems()) {
            OrderItem existingOrderItem = existingOrderItemMap.get(orderItemRequest.getProductId());
            if (existingOrderItem != null) {
                existingOrderItem.setQuantity(orderItemRequest.getQuantity());
                updatedOrderItems.add(existingOrderItem);
            } else {
                OrderItem newOrderItem = orderItemService.mapToOrderItem(orderItemRequest, oldOrder);
                updatedOrderItems.add(newOrderItem);
            }
        }
        orderItemRepository.saveAll(updatedOrderItems);
        oldOrder.setOrderItems(updatedOrderItems);

        Order savedOrder = orderRepository.save(oldOrder);
        return OrderConverter.toOrderDto(savedOrder);
    }


    public OrderDto addProduct(Long orderId, OrderItemRequest orderItem) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        List<OrderItem> orderItems = order.getOrderItems();


        boolean productExists = false;
        for (OrderItem item : orderItems) {
            if (item.getProduct().getId().equals(orderItem.getProductId())) {
                item.setQuantity(item.getQuantity() + orderItem.getQuantity());
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            OrderItem newOrderItem = orderItemService.save(orderItem, order);
            orderItems.add(newOrderItem);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);
        return OrderConverter.toOrderDto(updatedOrder);
    }

    public boolean removeProduct(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        Optional<OrderItem> itemToRemove = orderItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();


        if (itemToRemove.isPresent()) {
            orderItems.remove(itemToRemove.get());
            order.setUpdatedAt(LocalDateTime.now());

            ProductOrderId orderItemId = new ProductOrderId(orderId, productId);
            orderItemRepository.deleteById(orderItemId);
            orderRepository.save(order);
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.delete(order);
        return true;
    }
}