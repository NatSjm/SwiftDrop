package com.example.swiftdrop.service;
import com.example.swiftdrop.enums.OrderStatus;
import com.example.swiftdrop.model.Order;
import com.example.swiftdrop.model.OrderItem;
import com.example.swiftdrop.model.Product;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


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
        System.out.println(order);
        if (orders.containsKey(orderId)) {
            Order oldOrder = orders.get(orderId);
            oldOrder.setUpdatedAt(LocalDateTime.now());
            oldOrder.setCustomerId(order.getCustomerId());
            oldOrder.setStatus(order.getStatus());
            oldOrder.setOrderItems(order.getOrderItems());
            orders.put(orderId, oldOrder);
            return oldOrder;
        } else {
            return null;
        }

    }

    public Order getOrder(Long orderId) {
        Order order = orders.get(orderId);

        if (order != null) {
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
        return null;
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
        if (orders.containsKey(orderId)) {
            orders.remove(orderId);
            return true;
        } else {
            return false;
        }
    }

    public Long createOrderAndAddProducts(Order order) {
        Long orderId = createOrder(order.getCustomerId());

        for (OrderItem orderItem : order.getOrderItems()) {
            Long productId = orderItem.getProductId();
            int quantity = orderItem.getQuantity();
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setProductId(productId);
            newOrderItem.setQuantity(quantity);
            addProductToOrder(orderId, newOrderItem);
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

    public Order addProductToOrder(Long orderId, OrderItem orderItem) {
        Order order = orders.get(orderId);

        if (order != null) {
            List<OrderItem> orderItems = order.getOrderItems();

            boolean productExists = false;

            for (OrderItem item : orderItems) {
                if (item.getProductId().equals(orderItem.getProductId())) {
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
        return null;
    }
}