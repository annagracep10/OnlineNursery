package com.techphantomexample.usermicroservice.controller;

import com.techphantomexample.usermicroservice.entity.User;

public class CreateResponse {
    private String message;
    private Integer status;
    private User user;


    public CreateResponse(String message, Integer status , User user) {
        this.message = message;
        this.status = status;
        this.user = user;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
