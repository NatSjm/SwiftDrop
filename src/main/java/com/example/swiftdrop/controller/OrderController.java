package com.example.swiftdrop.controller;
import com.example.swiftdrop.model.Order;
import com.example.swiftdrop.model.OrderItem;
import com.example.swiftdrop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static java.util.Objects.isNull;


@RestController
@RequestMapping("/orders")

public class OrderController {

    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }


    @GetMapping("/{id}")
    public Order getById(@PathVariable String id) {
        try {
            long convertedId = Long.parseLong(id);
            Order order = orderService.getOrder(convertedId);
            if(isNull(order)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return order;
        } catch (Exception exception) {
            if (exception instanceof NumberFormatException) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                throw exception;
            }
        }
    }

    @PostMapping
    public Order save(@RequestBody Order order) {
        return orderService.create(order);
    }
    @PutMapping("/{id}")
    public Order update(@RequestBody Order order, @PathVariable String id) {
        try {
            long convertedId = Long.parseLong(id);
            Order newOrder = orderService.update(order, convertedId);
            if(isNull(newOrder)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return newOrder;
        } catch (Exception exception) {
            if (exception instanceof NumberFormatException) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                throw exception;
            }
        }
    }
    @PatchMapping("/{id}")
    public Order addProduct(@PathVariable String id, @RequestBody OrderItem orderItem) {
        try {
            long convertedId = Long.parseLong(id);
            return orderService.addProductToOrder(convertedId, orderItem);
        } catch (Exception exception) {
            if (exception instanceof NumberFormatException) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                throw exception;
            }
        }
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String orderId, @PathVariable String productId) {
        try {
            long convertedProductId = Long.parseLong(productId);
            long convertedOrderId = Long.parseLong(orderId);
            boolean removed = orderService.removeProduct(convertedOrderId, convertedProductId);
            if (removed) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            if (exception instanceof NumberFormatException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
                throw exception;
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            long convertedId = Long.parseLong(id);
            boolean removed = orderService.remove(convertedId);
            if (removed) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
                if (exception instanceof NumberFormatException) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                } else {
                    throw exception;
                }
        }
    }
}