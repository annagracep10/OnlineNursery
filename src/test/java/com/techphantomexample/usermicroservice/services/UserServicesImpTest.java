package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MockitoSettings
class UserServicesImpTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServicesImp userServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUser() {
        assertAll("Login tests",
                () -> assertLoginUser(new Login(null, "password"), "All fields are required", 400),
                () -> assertLoginUser(new Login("", "password"), "All fields are required", 400),
                () -> assertLoginUser(new Login("email@example.com", null), "All fields are required", 400),
                () -> assertLoginUser(new Login("email@example.com", ""), "All fields are required", 400),
                () -> assertLoginUserWithRepository(null, "email@example.com", "password", "Email does not exist", 401),
                () -> assertLoginUserWithRepository(createUserWithPassword("correctPassword"), "email@example.com", "wrongPassword", "Password does not match", 401),
                () -> assertLoginUserWithRepository(createUserWithPassword("correctPassword"), "email@example.com", "correctPassword", "Login Success", 200)
        );
    }

    @Test
    void createUser() {
        when(userRepository.existsByUserEmail(anyString())).thenReturn(false);
        User user = createUserWithPassword("Validpass123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        String result = userServices.createUser(user);
        assertEquals("User Created successfully", result);
        verify(userRepository, times(1)).existsByUserEmail(user.getUserEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_UserExists() {
        User user = new User();
        user.setUserEmail("test@example.com");

        when(userRepository.existsByUserEmail(anyString())).thenReturn(true);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userServices.createUser(user);
        });

        assertEquals("User with provided email ID exists", thrown.getMessage());
        verify(userRepository, times(1)).existsByUserEmail(anyString());
    }

    @Test
    void updateUser() {
        int userId = 1;
        User existingUser = createUserWithPassword("ExistingPass123");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        User newUserDetails = new User(1, "newName", "new@example.com", "NewPass123", "ADMIN", new Cart());

        String result = userServices.updateUser(userId, newUserDetails);

        assertEquals("User Updated Successfully", result);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        assertEquals("newName", existingUser.getUserFullName());
        assertEquals("new@example.com", existingUser.getUserEmail());
    }

    @Test
    void updateUser_UserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userServices.updateUser(1, new User());
        });

        assertEquals("User with ID 1 does not exist", thrown.getMessage());
        verify(userRepository, times(1)).existsById(anyInt());
    }

    @Test
    void deleteUser() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        String result = userServices.deleteUser(userId);
        assertEquals("User Deleted Successfully", result);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_UserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userServices.deleteUser(1);
        });

        assertEquals("User with ID 1 does not exist", thrown.getMessage());
        verify(userRepository, times(1)).existsById(anyInt());
    }

    @Test
    void getUser() {
        int userId = 1;
        User mockUser = createUserWithPassword("password");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        User result = userServices.getUser(userId);
        assertEquals(mockUser, result);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_UserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userServices.getUser(1);
        });

        assertEquals("User with ID 1 does not exist", thrown.getMessage());
        verify(userRepository, times(1)).existsById(anyInt());
    }

    @Test
    void getAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(1, "user1", "user1@example.com", "password1", "BUYER", new Cart()),
                new User(2, "user2", "user2@example.com", "password2", "SELLER", new Cart())
        );
        when(userRepository.findAll()).thenReturn(mockUsers);
        List<User> result = userServices.getAllUsers();
        assertEquals(mockUsers.size(), result.size());
        assertEquals(mockUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getCartByUserId() {
        int userId = 1;
        Cart mockCart = new Cart();
        User mockUser = new User(1, "username", "example@gmail.com", "password", "ADMIN", mockCart);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Cart result = userServices.getCartByUserId(userId);
        assertEquals(mockCart, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getCartByUserId_UserDoesNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userServices.getCartByUserId(1);
        });

        assertEquals("User with ID 1 does not exist", thrown.getMessage());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void validateUser_AllFieldsRequired() {
        User user = new User(1, "", "invalid", "Pass123", "INVALID", new Cart());
        assertThrows(UserOperationException.class, () -> {
            UserOperationException.validateUser(user, userRepository);
        });
    }

    @Test
    void validateUser_InvalidEmail() {
        User user = new User(1, "username", "invalid", "Pass123", "ADMIN", new Cart());
        assertThrows(UserOperationException.class, () -> {
            UserOperationException.validateUser(user, userRepository);
        });
    }

    @Test
    void validateUser_InvalidPassword() {
        User user = new User(1, "username", "email@example.com", "short", "ADMIN", new Cart());
        assertThrows(UserOperationException.class, () -> {
            UserOperationException.validateUser(user, userRepository);
        });
    }

    @Test
    void validateUser_InvalidUserRole() {
        User user = new User(1, "username", "email@example.com", "ValidPass1", "INVALID", new Cart());
        assertThrows(UserOperationException.class, () -> {
            UserOperationException.validateUser(user, userRepository);
        });
    }

    @Test
    void validateUser_ValidUser() {
        User user = new User(1, "username", "email@example.com", "ValidPass1", "ADMIN", new Cart());
        assertDoesNotThrow(() -> {
            UserOperationException.validateUser(user, userRepository);
        });
    }

    private void assertLoginUser(Login login, String expectedMessage, int expectedStatus) {
        CreateResponse response = userServices.loginUser(login);
        assertEquals(expectedMessage, response.getMessage());
        assertEquals(expectedStatus, response.getStatus());
    }

    private void assertLoginUserWithRepository(User user, String email, String password, String expectedMessage, int expectedStatus) {
        when(userRepository.findByUserEmail(email)).thenReturn(user);
        Login login = new Login(email, password);
        CreateResponse response = userServices.loginUser(login);
        assertEquals(expectedMessage, response.getMessage());
        assertEquals(expectedStatus, response.getStatus());
    }

    private User createUserWithPassword(String password) {
        return new User(1, "username", "valid@example.com", BCrypt.hashpw(password, BCrypt.gensalt()), "ADMIN", new Cart());
    }
}
