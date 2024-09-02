package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.entity.OrderItem;
import com.techphantomexample.usermicroservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Query("SELECT p.productId, p.productName, SUM(p.quantity) as totalQuantity " +
            "FROM OrderItem p GROUP BY p.productId, p.productName " +
            "ORDER BY SUM(p.quantity) DESC")
    List<Object[]> findTopProducts();
}
