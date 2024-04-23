package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

@Slf4j
public class UserOperationException extends RuntimeException
{


    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(UserOperationException.class);

    public UserOperationException(String message) {
        super(message);
        log.error(message);
    }

    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

    public static void validateUser(User user ,UserRepository userRepository ) {
        if (isNullOrEmpty(user.getUserFullName()) || isNullOrEmpty(user.getUserEmail()) || isNullOrEmpty(user.getUserPassword()) || isNullOrEmpty(user.getUserRole())) {
            throw new UserOperationException("All fields are required");

        }

        if (!isValidEmail(user.getUserEmail())) {
            throw new UserOperationException("Invalid email address");
        }

        if (existsByEmail(user.getUserEmail(), userRepository)) {
            throw new UserOperationException("User with provided Email ID exists");
        }

        if (!isValidPassword(user.getUserPassword())) {
            throw new UserOperationException("Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit");
        }

        if (!isValidUserRole(user.getUserRole())) {
            throw new UserOperationException("User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER");
        }
    }

    private static boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private static boolean existsByEmail(String userEmail, UserRepository userRepository) {
        return userRepository.existsByUserEmail(userEmail);
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
