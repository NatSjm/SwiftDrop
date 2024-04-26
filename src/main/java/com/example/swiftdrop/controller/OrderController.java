package com.example.swiftdrop.controller;

import com.example.swiftdrop.dto.OrderDto;
import com.example.swiftdrop.request.CreateOrderRequest;
import com.example.swiftdrop.request.OrderItemRequest;
import com.example.swiftdrop.request.UpdateOrderRequest;
import com.example.swiftdrop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        try {
            return orderService.getOrder(id);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @PostMapping
    public OrderDto save(@RequestBody CreateOrderRequest request) {
       try {
           return orderService.save(request);
        } catch (Exception exception) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
       }

    }

    @PutMapping("/{id}")
    public OrderDto update(@RequestBody UpdateOrderRequest request, @PathVariable Long id) {
        try {
            return orderService.update(request, id);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @PatchMapping("/{id}")
    public OrderDto addProduct(@PathVariable Long id, @RequestBody OrderItemRequest orderItem) {
        try {
            return orderService.addProduct(id, orderItem);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long orderId, @PathVariable Long productId) {
        try {
            boolean removed = orderService.removeProduct(orderId, productId);
            if (removed) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            boolean removed = orderService.remove(id);
            if (removed) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }
}