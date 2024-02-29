package com.example.swiftdrop.repository;

import com.example.swiftdrop.entity.OrderItem;
import com.example.swiftdrop.entity.ProductOrderId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, ProductOrderId> {
    void deleteById(@NonNull ProductOrderId productOrderId);
}