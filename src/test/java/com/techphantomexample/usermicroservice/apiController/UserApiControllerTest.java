package com.techphantomexample.usermicroservice.apiController;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        Login login = new Login("valid_username", "valid_password");
        CreateResponse expectedResponse = new CreateResponse("success", HttpStatus.OK.value(), null);

        when(userService.loginUser(any(Login.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        Login login = new Login("invalid_username", "invalid_password");
        CreateResponse expectedResponse = new CreateResponse("failure", HttpStatus.UNAUTHORIZED.value(), null);

        when(userService.loginUser(any(Login.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", "USER",new Cart());
        CreateResponse expectedResponse = new CreateResponse("success", HttpStatus.OK.value(), user);

        when(userService.createUser(any(User.class))).thenReturn("success");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testUpdateUser() throws Exception {
        int userId = 1;
        User user = new User(userId, "John Doe", "john.doe@example.com", "password", "USER",new Cart());
        CreateResponse expectedResponse = new CreateResponse("success", HttpStatus.OK.value(), user);

        when(userService.updateUser(eq(userId), any(User.class))).thenReturn("success");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/update/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


    @Test
    public void testDeleteUser() throws Exception {
        int userId = 1;
        CreateResponse expectedResponse = new CreateResponse("success", HttpStatus.OK.value(), null);

        when(userService.deleteUser(eq(userId))).thenReturn("success");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/delete/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }



    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                new User(1, "John Doe", "john.doe@example.com", "password", "USER",new Cart()),
                new User(2, "Jane Smith", "jane.smith@example.com", "password", "USER",new Cart())
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/list"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void testGetUserById() throws Exception {
        int userId = 1;
        User user = new User(userId, "John Doe", "john.doe@example.com", "password", "USER",new Cart());

        when(userService.getUser(eq(userId))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testHandleUserOperationException() throws Exception {
        int userId = 1;
        String errorMessage = "User with ID " + userId + " does not exist";
        UserOperationException exception = new UserOperationException(errorMessage);

        when(userService.getUser(eq(userId))).thenThrow(exception);

        CreateResponse expectedResponse = new CreateResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{userId}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


}

