package com.techphantomexample.usermicroservice.exception;

import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;


public class UserOperationException extends RuntimeException
{
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(UserOperationException.class);

    public UserOperationException(String message) {
        super(message);
        log.error(message);
    }

}
