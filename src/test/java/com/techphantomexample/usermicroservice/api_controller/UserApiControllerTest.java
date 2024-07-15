package com.techphantomexample.usermicroservice.api_controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginUser() throws Exception {
        Login login = new Login("john.doe@example.com", "password");
        CreateResponse response = new CreateResponse("Login successful", HttpStatus.OK.value(), null);

        when(userService.loginUser(any(Login.class))).thenReturn(response);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", "USER", new Cart());
        CreateResponse response = new CreateResponse("User Created successfully", HttpStatus.CREATED.value(), user);

        when(userService.createUser(any(User.class))).thenReturn("User Created successfully");

        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User Created successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "newpassword", "USER", new Cart());
        CreateResponse response = new CreateResponse("User Updated Successfully", HttpStatus.OK.value(), user);

        when(userService.updateUser(anyInt(), any(User.class))).thenReturn("User Updated Successfully");

        mockMvc.perform(put("/api/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User Updated Successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    void testDeleteUser() throws Exception {
        CreateResponse response = new CreateResponse("User Deleted Successfully", HttpStatus.OK.value(), null);

        when(userService.deleteUser(anyInt())).thenReturn("User Deleted Successfully");

        mockMvc.perform(delete("/api/user/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User Deleted Successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User(1, "John Doe", "john.doe@example.com", "password", "USER", new Cart());
        User user2 = new User(2, "Jane Doe", "jane.doe@example.com", "password", "USER", new Cart());
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/user/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userFullName").value("John Doe"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].userFullName").value("Jane Doe"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", "USER", new Cart());

        when(userService.getUser(anyInt())).thenReturn(user);

        mockMvc.perform(get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userFullName").value("John Doe"))
                .andExpect(jsonPath("$.userEmail").value("john.doe@example.com"));
    }

    @Test
    void testGetCartByUserId() throws Exception {
        Cart cart = new Cart();
        cart.setId(1);

        when(userService.getCartByUserId(anyInt())).thenReturn(cart);

        mockMvc.perform(get("/api/user/1/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateUser_UserOperationException() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", "USER", new Cart());

        when(userService.createUser(any(User.class))).thenThrow(new UserOperationException("User with provided email ID exists"));

        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with provided email ID exists"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testUpdateUser_UserOperationException() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", "USER", new Cart());

        when(userService.updateUser(anyInt(), any(User.class))).thenThrow(new UserOperationException("User with ID 1 does not exist"));

        mockMvc.perform(put("/api/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with ID 1 does not exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testDeleteUser_UserOperationException() throws Exception {
        when(userService.deleteUser(anyInt())).thenThrow(new UserOperationException("User with ID 1 does not exist"));

        mockMvc.perform(delete("/api/user/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with ID 1 does not exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void testGetUserById_UserOperationException() throws Exception {
        when(userService.getUser(anyInt())).thenThrow(new UserOperationException("User with ID 1 does not exist"));

        mockMvc.perform(get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with ID 1 does not exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }
}
