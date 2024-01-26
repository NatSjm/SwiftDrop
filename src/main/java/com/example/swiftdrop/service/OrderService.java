package com.example.swiftdrop.service;
import com.example.swiftdrop.enums.OrderStatus;
import com.example.swiftdrop.model.Order;
import com.example.swiftdrop.model.OrderItem;
import com.example.swiftdrop.model.Product;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    public Order create(Order order) {
        Long orderId = createOrderAndAddProducts(order);
        return orders.get(orderId);
    }

    public Order update(Order order, Long orderId) {
        Order oldOrder = orders.get(orderId);
        if (isNull(order)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
            oldOrder.setUpdatedAt(LocalDateTime.now());
            oldOrder.setCustomerId(order.getCustomerId());
            oldOrder.setStatus(order.getStatus());
            oldOrder.setOrderItems(order.getOrderItems());
            orders.put(orderId, oldOrder);
            return oldOrder;
    }

    public Order getOrder(Long orderId) {
        Order order = orders.get(orderId);
        if (isNull(order)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            Product product = productService.getProductById(productId);

            if (product != null) {
                orderItem.setProductName(product.getName());
                orderItem.setProductPrice(product.getPrice());
            }
        }
        return order;
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
        { return orders.remove(orderId) != null; }
    }

    private Long createOrderAndAddProducts(Order order) {
        Long orderId = createOrder(order.getCustomerId());

        for (OrderItem orderItem : order.getOrderItems()) {
            Long productId = orderItem.getProductId();
            int quantity = orderItem.getQuantity();
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setProductId(productId);
            newOrderItem.setQuantity(quantity);
            addProduct(orderId, newOrderItem);
        }

        return orderId;

    }

    public Long createOrder(Long customerId) {
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

        if (order == null) { return null; }
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
}