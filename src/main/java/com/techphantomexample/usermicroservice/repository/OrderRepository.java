package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.entity.OrderStatus;
import com.techphantomexample.usermicroservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository  extends JpaRepository<Orders, Integer> {

    List<Orders> findByUserId(int userId);
    Orders findByRazorpayOrderId(String razorpayOrderId);
    @Query("SELECT o FROM Orders o WHERE o.userId = :userId AND o.status = :status")
    Orders findByUserIdAndStatus(@Param("userId") int userId, @Param("status") OrderStatus status);
}
