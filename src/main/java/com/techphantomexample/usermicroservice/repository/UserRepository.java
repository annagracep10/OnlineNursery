package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository <User, Integer> {
    boolean existsByUserEmail(String userEmail);
    User findByUserEmail(String userEmail);


}
