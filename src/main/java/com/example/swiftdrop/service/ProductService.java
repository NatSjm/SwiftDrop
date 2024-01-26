package com.example.swiftdrop.service;
import com.example.swiftdrop.model.Product;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;


@Service
public class ProductService {
    private final Map<Long, Product> products = new HashMap<>();

    public ProductService() {
        Product product1 = new Product();
        product1.setId(101L);
        product1.setName("Product 1");
        product1.setPrice(20.0);

        Product product2 = new Product();
        product2.setId(102L);
        product2.setName("Product 2");
        product2.setPrice(30.0);

        Product product3 = new Product();
        product3.setId(103L);
        product3.setName("Product 3");
        product3.setPrice(40.0);


        products.put(product1.getId(), product1);
        products.put(product2.getId(), product2);
        products.put(product3.getId(), product3);
    }

    public Product getProductById(Long id) {
        return products.get(id);
    }
}