package com.example.swiftdrop.service;

import com.example.swiftdrop.enums.OrderStatus;
import com.example.swiftdrop.entity.OrderItem;
import com.example.swiftdrop.entity.Product;
import com.example.swiftdrop.repository.OrderItemRepository;
import com.example.swiftdrop.repository.OrderRepository;
import com.example.swiftdrop.entity.Order;
import com.example.swiftdrop.dto.OrderDto;
import com.example.swiftdrop.converter.OrderConverter;
import com.example.swiftdrop.repository.ProductRepository;
import com.example.swiftdrop.request.CreateOrderRequest;
import com.example.swiftdrop.request.OrderItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {


    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderService orderService;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void getOrderReturnsOrderDtoWhenOrderExists() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDto expectedOrderDto = OrderConverter.toOrderDto(order);
        OrderDto actualOrderDto = orderService.getOrder(orderId);

        assertEquals(expectedOrderDto, actualOrderDto);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void getOrderThrowsExceptionWhenOrderDoesNotExist() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.getOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    public void saveReturnsOrderDtoWhenOrderItemsAreNotNull() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-03-03T23:34:42.536815600Z"), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now(fixedClock);


        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .customerId(1L)
                .orderItems(List.of(OrderItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        Order order = Order.builder()
                .customerId(createOrderRequest.getCustomerId())
                .createdAt(now)
                .updatedAt(now)
                .status(OrderStatus.NEW)
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .customerId(createOrderRequest.getCustomerId())
                .createdAt(now)
                .updatedAt(now)
                .status(OrderStatus.NEW)
                .orderItems(List.of(OrderItem.builder().product(new Product()).quantity(1).build()))
                .build();

        List<OrderItem> orderItems = List.of(OrderItem.builder().order(order).product(new com.example.swiftdrop.entity.Product()).quantity(1).build());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new com.example.swiftdrop.entity.Product()));
        when(orderItemService.saveAll(createOrderRequest.getOrderItems(), savedOrder)).thenReturn(orderItems);

        OrderDto expectedOrderDto = OrderConverter.toOrderDto(savedOrder);
        OrderDto actualOrderDto = orderService.save(createOrderRequest);

        assertEquals(expectedOrderDto.getId(), actualOrderDto.getId());
        assertEquals(expectedOrderDto.getCustomerId(), actualOrderDto.getCustomerId());
        assertEquals(expectedOrderDto.getStatus(), actualOrderDto.getStatus());
        verify(orderItemService, times(1)).saveAll(createOrderRequest.getOrderItems(), savedOrder);
    }



}