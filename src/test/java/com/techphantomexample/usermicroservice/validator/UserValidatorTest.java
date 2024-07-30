package com.techphantomexample.usermicroservice.validator;

import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    UserRepository userRepository;

    @Test
    public void testValidateUser_AllFieldsAreRequired() {
        UserEntity user = new UserEntity();

        assertThrows(UserOperationException.class, () -> UserValidator.validateUser(user, userRepository), "All fields are required");
    }

    @Test
    public void testValidateUser_InvalidEmail() {
        UserEntity user = new UserEntity();
        user.setUserFullName("John Doe");
        user.setUserEmail("invalid-email");
        user.setUserPassword("Password1");
        user.setUserRole("ADMIN");

        assertThrows(UserOperationException.class, () -> UserValidator.validateUser(user, userRepository), "Invalid email address");
    }

    @Test
    public void testValidateUser_InvalidPassword() {
        UserEntity user = new UserEntity();
        user.setUserFullName("John Doe");
        user.setUserEmail("john.doe@example.com");
        user.setUserPassword("short");
        user.setUserRole("ADMIN");

        assertThrows(UserOperationException.class, () -> UserValidator.validateUser(user, userRepository), "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit");
    }

    @Test
    public void testValidateUser_InvalidUserRole() {
        UserEntity user = new UserEntity();
        user.setUserFullName("John Doe");
        user.setUserEmail("john.doe@example.com");
        user.setUserPassword("Password1");
        user.setUserRole("INVALID_ROLE");

        assertThrows(UserOperationException.class, () -> UserValidator.validateUser(user, userRepository), "User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER");
    }

    @Test
    public void testValidateUser_ValidUser() {
        UserEntity user = new UserEntity();
        user.setUserFullName("John Doe");
        user.setUserEmail("john.doe@example.com");
        user.setUserPassword("Password1");
        user.setUserRole("ADMIN");

        assertDoesNotThrow(() -> UserValidator.validateUser(user, userRepository));
    }


}