package com.techphantomexample.usermicroservice.validator;

import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.repository.UserRepository;

import java.util.Arrays;

public class UserValidator {

    public static void validateUser(UserEntity user , UserRepository userRepository ) {
        if (isNullOrEmpty(user.getUserFullName()) || isNullOrEmpty(user.getUserEmail()) || isNullOrEmpty(user.getUserPassword()) || isNullOrEmpty(user.getUserRole())) {
            throw new UserOperationException("All fields are required");
        }
        if (!isValidEmail(user.getUserEmail())) {
            throw new UserOperationException("Invalid email address");
        }
        if (!isValidPassword(user.getUserPassword())) {
            throw new UserOperationException("Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit");
        }
        if (!isValidUserRole(user.getUserRole())) {
            throw new UserOperationException("User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER");
        }
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }


    private static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches("^(?=.*[A-Z])(?=.*\\d).+$");
    }

    private static boolean isValidUserRole(String userRole) {
        return userRole != null && Arrays.asList("ADMIN", "SUPERVISOR", "BUYER", "SELLER").contains(userRole.toUpperCase());
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
