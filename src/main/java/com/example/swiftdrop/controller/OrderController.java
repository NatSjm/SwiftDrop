package com.example.swiftdrop.controller;

import com.example.swiftdrop.model.*;
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
    public ExtendedOrder getById(@PathVariable Long id) {
        try {
            return orderService.getOrder(id);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @PostMapping
    public Order save(@RequestBody CreateOrderRequest order) {
       try {
           return orderService.create(order);
        } catch (Exception exception) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
       }

    }

    @PutMapping("/{id}")
    public Order update(@RequestBody UpdateOrderRequest order, @PathVariable Long id) {
        try {
            return orderService.update(order, id);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @PatchMapping("/{id}")
    public Order addProduct(@PathVariable Long id, @RequestBody OrderItem orderItem) {
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