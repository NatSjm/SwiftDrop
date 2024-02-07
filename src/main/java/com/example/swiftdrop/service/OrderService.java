package com.example.swiftdrop.service;

import com.example.swiftdrop.enums.OrderStatus;
import com.example.swiftdrop.model.*;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;


@Service
@Getter
public class OrderService {
    private final Map<Long, Order> orders = new HashMap<>();
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    private final ProductService productService;


    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    public Order create(CreateOrderRequest order) {
        checkRequestBodyForNull(order);
        Long orderId = createOrderAndAddProducts(order);
        return orders.get(orderId);
    }

    public Order update(UpdateOrderRequest order, Long orderId) {
        checkRequestBodyForNull(order);
        Order oldOrder = getOrderById(orderId);
        oldOrder.setUpdatedAt(LocalDateTime.now());
        oldOrder.setCustomerId(order.getCustomerId());
        oldOrder.setStatus(order.getStatus());
        oldOrder.setOrderItems(order.getOrderItems());
        orders.put(orderId, oldOrder);
        return oldOrder;
    }

    public ExtendedOrder getOrder(Long orderId) {

        Order order = getOrderById(orderId);
        List<OrderItem> orderItems = order.getOrderItems();
        System.out.println("orderItems" + orderItems);
        List<ExtendedOrderItem> extendedOrderItems = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            Product product = productService.getProductById(productId);

            if (product != null) {
                ExtendedOrderItem extendedOrderItem = new ExtendedOrderItem();
                extendedOrderItem.setProductId(productId);
                extendedOrderItem.setProductName(product.getName());
                extendedOrderItem.setProductPrice(product.getPrice());
                extendedOrderItem.setQuantity(orderItem.getQuantity());

                extendedOrderItems.add(extendedOrderItem);
            }
        }

        ExtendedOrder extendedOrder = new ExtendedOrder();
        extendedOrder.setId(order.getId());
        extendedOrder.setCustomerId(order.getCustomerId());
        extendedOrder.setOrderItems(extendedOrderItems);
        extendedOrder.setStatus(order.getStatus());
        extendedOrder.setUpdatedAt(order.getUpdatedAt());
        extendedOrder.setCreatedAt(order.getCreatedAt());

        return extendedOrder;
    }


    public boolean removeProduct(Long orderId, Long productId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        List<OrderItem> orderItems = order.getOrderItems();
        Optional<OrderItem> itemToRemove = orderItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        return itemToRemove.map(orderItems::remove).orElse(false);
    }


    public boolean remove(Long orderId) {
        return orders.remove(orderId) != null;
    }

    private Long createOrderAndAddProducts(CreateOrderRequest order) {
        Long orderId = createOrder(order.getCustomerId());
        List<OrderItem> orderItems = order.getOrderItems();
        if (isNull(orderItems)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            int quantity = orderItem.getQuantity();
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setProductId(productId);
            newOrderItem.setQuantity(quantity);
            addProduct(orderId, newOrderItem);
        }

        return orderId;

    }

    private Long createOrder(Long customerId) {
        Long orderId = orderIdGenerator.getAndIncrement();

        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setOrderItems(new ArrayList<>());
        orders.put(orderId, order);

        return orderId;
    }

    public Order addProduct(Long orderId, OrderItem orderItem) {
        Order order = orders.get(orderId);

        if (order == null) {
            return null;
        }
        List<OrderItem> orderItems = order.getOrderItems();

        boolean productExists = false;

        for (OrderItem item : orderItems) {
            if (item != null && item.getProductId().equals(orderItem.getProductId())) {
                item.setQuantity(item.getQuantity() + orderItem.getQuantity());
                productExists = true;
                break;
            }
        }
        if (!productExists) {
            orderItems.add(orderItem);
        }
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    private Order getOrderById(Long orderId) {
        Order order = orders.get(orderId);
        if (isNull(order)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return order;
    }

    private void checkRequestBodyForNull(OrderRequest request) {
        if (isNull(request)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }
}