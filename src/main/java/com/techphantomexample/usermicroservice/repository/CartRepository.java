package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByUser_UserId(int userId);
}
