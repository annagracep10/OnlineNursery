package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.entity.UserEntity;

import java.util.List;

public interface UserService
{
    public String createUser(UserEntity user);
    public String updateUser(int userId , UserEntity user);
    public String deleteUser(int userId);
    public UserEntity getUser(int userId);
    public List<UserEntity> getAllUsers();
    public Cart getCartByUserId(int userId);
    CreateResponse loginUser(Login login);
    int getUserIdByEmail(String userEmail);
}
