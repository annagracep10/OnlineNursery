package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class UserServicesImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private UserServicesImp userService;

    private User createUserWithPassword(String password) {
        return new User(1, "username", "valid@example.com", BCrypt.hashpw(password, BCrypt.gensalt()), "ADMIN", new Cart());
    }

    @Test
    void testLoginUser_Fail_EmptyFields() {
        Login login = new Login("","");

        CreateResponse response = userService.loginUser(login);

        assertEquals("All fields are required", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getUser());

    }

    @Test
    void testLoginUser_Fail_NonExistingUser() {
        Login login = new Login("wrong@example.com","password");
        when(userRepository.findByUserEmail(login.getUserEmail())).thenReturn(null);

        CreateResponse response = userService.loginUser(login);

        assertEquals("Email does not exist", response.getMessage());
        assertEquals(401, response.getStatus());
        assertNull(response.getUser());

    }

    @Test
    void testLoginUser_Fail_IncorrectPassword() {
        User user = createUserWithPassword("Password");
        Login login = new Login("valid@example.com","Wrong");
        when(userRepository.findByUserEmail(login.getUserEmail())).thenReturn(user);

        CreateResponse response = userService.loginUser(login);

        assertEquals("Password does not match", response.getMessage());
        assertEquals(401, response.getStatus());
        assertNull(response.getUser());
    }

    @Test
    void testLoginUser_Success() {
        User user = createUserWithPassword("password");
        Login login = new Login("valid@example.com","password");
        when(userRepository.findByUserEmail(login.getUserEmail())).thenReturn(user);

        CreateResponse response = userService.loginUser(login);

        assertEquals("Login Success", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(user, response.getUser());

    }

    @Test
    void testCreateUser_Success() {

    }

    @Test
    void testCreateUser_Failure() {

    }
    @Test
    void testUpdateUser_Failure() {

    }

    @Test
    void testUpdateUser_Success() {

    }


    @Test
    void testDeleteUser_Success() {
        User user = createUserWithPassword("password");
        when(userRepository.existsById(user.getUserId())).thenReturn(true);

        String result = userService.deleteUser(user.getUserId());

        assertEquals("User Deleted Successfully", result);
        verify(userRepository, times(1)).existsById(user.getUserId());
        verify(userRepository, times(1)).deleteById(user.getUserId());
    }

    @Test
    void testDeleteUser_Failure() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userService.deleteUser(1);
        });

        assertEquals("User with ID 1 does not exist", thrown.getMessage());
        verify(userRepository, times(1)).existsById(anyInt());
    }

    @Test
    void testGetUser_Success() {
        User mockUser = createUserWithPassword("password");
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.getUser(userId);

        assertEquals(mockUser, result);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUser_UserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userService.getUser(1);
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

        List<User> result = userService.getAllUsers();

        assertEquals(mockUsers.size(), result.size());
        assertEquals(mockUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetCartByUserId_CartExists() {
        int userId = 1;
        Cart mockCart = new Cart();
        when(cartRepository.findByUser_UserId(userId)).thenReturn(mockCart);

        Cart result = userService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(mockCart, result);
        verify(cartRepository, times(1)).findByUser_UserId(userId);
        verify(userRepository, never()).findById(userId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testGetCartByUserId_CartDoesNotExist_UserExists() {
        User mockUser = createUserWithPassword("password");
        when(cartRepository.findByUser_UserId(mockUser.getUserId())).thenReturn(null);
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        Cart mockCart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        Cart result = userService.getCartByUserId(mockUser.getUserId());

        assertNotNull(result);
        assertEquals(mockCart, result);
        verify(cartRepository, times(1)).findByUser_UserId(mockUser.getUserId());
        verify(userRepository, times(1)).findById(mockUser.getUserId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testGetCartByUserId_UserNotFound() {
        int userId = 1;
        when(cartRepository.findByUser_UserId(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(null);

        UserOperationException exception = assertThrows(UserOperationException.class, () -> {
            userService.getCartByUserId(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(cartRepository, times(1)).findByUser_UserId(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(cartRepository, never()).save(any(Cart.class));
    }




}
