package com.techphantomexample.usermicroservice.repository;

import com.techphantomexample.usermicroservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository  extends JpaRepository <UserEntity, Integer> {
    boolean existsByUserEmail(String userEmail);
    UserEntity findByUserEmail(String userEmail);

}
