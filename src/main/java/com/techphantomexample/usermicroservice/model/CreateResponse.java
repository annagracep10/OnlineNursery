package com.techphantomexample.usermicroservice.model;

import com.techphantomexample.usermicroservice.entity.UserEntity;

public class CreateResponse {
    private String message;
    private Integer status;
    private UserEntity user;


    public CreateResponse(String message, Integer status , UserEntity user) {
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
