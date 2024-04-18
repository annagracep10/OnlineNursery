package com.techphantomexample.usermicroservice.controller;

public class CreateResponse {
    private String message;
    private Integer status;

    public CreateResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
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


}
