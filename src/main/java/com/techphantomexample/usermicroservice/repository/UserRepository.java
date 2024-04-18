package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository <User, Integer> {
    boolean existsByUserEmail(String userEmail);
}
