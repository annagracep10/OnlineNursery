package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    void testLoginUser_Fail_EmptyEmail() {
        Login login = new Login("","Password1");

        CreateResponse response = userService.loginUser(login);

        assertEquals("All fields are required", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getUser());

    }

    @Test
    void testLoginUser_Fail_EmptyPassword() {
        Login login = new Login("valid@gmail.com","");

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
        User user = createUserWithPassword("password");
        when(userRepository.existsByUserEmail(user.getUserEmail())).thenReturn(false);

        String result = userService.createUser(user);

        assertEquals("User Created successfully", result);
        verify(userRepository, times(1)).existsByUserEmail(user.getUserEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        User user = createUserWithPassword("password");
        when(userRepository.existsByUserEmail(user.getUserEmail())).thenReturn(true);

        UserOperationException exception = assertThrows(UserOperationException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("User with provided email ID exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUserEmail(user.getUserEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = createUserWithPassword("password");
        User newUserDetails = new User(1, "Updated Username", "updated@example.com", BCrypt.hashpw("password", BCrypt.gensalt()), "ADMIN", new Cart());
        when(userRepository.existsById(existingUser.getUserId())).thenReturn(true);
        when(userRepository.findById(existingUser.getUserId())).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(newUserDetails);

        String result = userService.updateUser(1, newUserDetails);

        assertEquals("User Updated Successfully", result);
        assertEquals(newUserDetails.getUserFullName(), existingUser.getUserFullName());
        assertEquals(newUserDetails.getUserEmail(), existingUser.getUserEmail());
        assertEquals(newUserDetails.getUserRole(), existingUser.getUserRole());
        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User newUserDetails = createUserWithPassword("password");
        when(userRepository.existsById(newUserDetails.getUserId())).thenReturn(false);

        UserOperationException thrown = assertThrows(UserOperationException.class, () -> {
            userService.updateUser(newUserDetails.getUserId(), newUserDetails);
        });

        assertEquals("User with ID " + newUserDetails.getUserId() + " does not exist", thrown.getMessage());
        verify(userRepository, times(1)).existsById(newUserDetails.getUserId());
        verify(userRepository, never()).findById(newUserDetails.getUserId());
        verify(userRepository, never()).save(any(User.class));
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
    void testGetCartByUserId_CartExists() {
        User user = createUserWithPassword("password");
        Cart mockCart = new Cart();
        mockCart.setUser(user);
        when(cartRepository.findByUser_UserId(user.getUserId())).thenReturn(mockCart);

        Cart cart = userService.getCartByUserId(user.getUserId());

        assertNotNull(cart);
        assertEquals(user, cart.getUser());

    }

    @Test
    void testGetCartByUserId_CartDoesNotExist_UserExists() {
        User user = createUserWithPassword("password");

        when(cartRepository.findByUser_UserId(user.getUserId())).thenReturn(null);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = userService.getCartByUserId(user.getUserId());

        assertNotNull(cart);
        assertEquals(user, cart.getUser());
        verify(cartRepository, times(1)).findByUser_UserId(user.getUserId());
        verify(userRepository, times(1)).findById(user.getUserId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCartByUserId_CartDoesNotExist_UserDoesNotExist() {
        int userId = 1;
        when(cartRepository.findByUser_UserId(userId)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserOperationException exception = assertThrows(UserOperationException.class, () -> {
            userService.getCartByUserId(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(cartRepository, times(1)).findByUser_UserId(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(cartRepository, never()).save(any(Cart.class));
    }




}
