package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.controller.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.entity.User;

import java.util.List;

public interface UserService
{
    public String createUser(User user);
    public String updateUser(int userId ,User user);
    public String deleteUser(int userId);
    public User getUser(int userId);
    public List<User> getAllUsers();

    CreateResponse loginUser(Login login);
}
