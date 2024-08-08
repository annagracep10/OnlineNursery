package com.techphantomexample.usermicroservice.validator;

import com.techphantomexample.usermicroservice.exception.UserOperationException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    public static void isValidPassword(String password) {
        boolean x = password != null && password.length() >= 8 && password.matches("^(?=.*[A-Z])(?=.*\\d).+$");
        if (!x) throw new UserOperationException("Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit");
    }
}
