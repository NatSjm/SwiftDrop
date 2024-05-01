package com.example.swiftdrop.service;

import com.example.swiftdrop.entity.Order;
import com.example.swiftdrop.entity.OrderItem;
import com.example.swiftdrop.repository.OrderItemRepository;
import com.example.swiftdrop.repository.ProductRepository;
import com.example.swiftdrop.request.OrderItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemService orderItemService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void saveAllReturnsEmptyListWhenOrderItemsRequestIsNull() {
        Order order = new Order();
        List<OrderItem> result = orderItemService.saveAll(null, order);
        assertTrue(result.isEmpty());
    }

    @Test
    public void saveAllReturnsOrderItemsWhenOrderItemsRequestIsNotNull() {
        Order order = new Order();
        List<OrderItemRequest> orderItemsRequest = List.of(OrderItemRequest.builder().productId(1L).quantity(1).build());
        List<OrderItem> orderItems = List.of(OrderItem.builder().order(order).product(new com.example.swiftdrop.entity.Product()).quantity(1).build());
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new com.example.swiftdrop.entity.Product()));
        when(orderItemRepository.saveAll(anyList())).thenReturn(orderItems);
        List<OrderItem> result = orderItemService.saveAll(orderItemsRequest, order);
        assertEquals(orderItems, result);
    }

    @Test
    public void saveAllThrowsExceptionWhenProductNotFound() {
        Order order = new Order();
        List<OrderItemRequest> orderItemsRequest = List.of(new OrderItemRequest());
        when(productRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("Product not found"));
        assertThrows(IllegalArgumentException.class, () -> orderItemService.saveAll(orderItemsRequest, order));
    }

    @Test
    public void mapToOrderItemReturnsOrderItem() {
        Order order = new Order();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder().productId(1L).quantity(1).build();
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new com.example.swiftdrop.entity.Product()));
        OrderItem result = orderItemService.mapToOrderItem(orderItemRequest, order);
        assertNotNull(result);
    }

    @Test
    public void saveReturnsOrderItem() {
        Order order = new Order();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder().productId(1L).quantity(1).build();
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new com.example.swiftdrop.entity.Product()));
        when(orderItemRepository.save(Mockito.any())).thenReturn(OrderItem.builder().order(order).product(new com.example.swiftdrop.entity.Product()).quantity(1).build());
        OrderItem result = orderItemService.save(orderItemRequest, order);
        assertNotNull(result);
    }

}
