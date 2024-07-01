package com.techphantomexample.usermicroservice;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.services.UserServicesImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserServicesImpTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServicesImp userServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginUser() {
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
    public void testCreateUser() {
        when(userRepository.existsByUserEmail(anyString())).thenReturn(false);
        User user = createUserWithPassword("Validpass123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userServices.createUser(user);

        assertEquals("User Created successfully", result);
        verify(userRepository, times(1)).existsByUserEmail(user.getUserEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() {
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
    public void testDeleteUser() {
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);

        String result = userServices.deleteUser(userId);

        assertEquals("User Deleted Successfully", result);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testGetUser() {
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
    public void testGetAllUsers() {
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
    public void testGetCartByUserId() {
        int userId = 1;
        Cart mockCart = new Cart();
        User mockUser = new User(1, "username", "example@gmail.com", "password", "ADMIN", mockCart);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Cart result = userServices.getCartByUserId(userId);

        assertEquals(mockCart, result);
        verify(userRepository, times(1)).findById(userId);
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
