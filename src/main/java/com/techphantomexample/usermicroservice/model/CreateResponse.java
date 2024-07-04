package com.techphantomexample.usermicroservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techphantomexample.usermicroservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateResponse {
    private String message;
    private Integer status;
    @JsonIgnore
    private User user;
}
