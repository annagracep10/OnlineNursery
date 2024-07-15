package com.techphantomexample.usermicroservice.web_controller;

import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@MockitoSettings
class UserWebControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private UserWebController userWebController;


    @BeforeEach
    void setUp() {
    }

    @Test
    void showLoginPage() {

        String viewName = userWebController.showLoginPage(model);

        assertEquals("login", viewName);
        verify(model, times(1)).addAttribute(eq("login"), any(Login.class));
    }

    @Test
    void loginUser_Success() {
        Login login = new Login();
        User user = new User();
        CreateResponse response = new CreateResponse("Success", 200, user);
        when(userService.loginUser(any(Login.class))).thenReturn(response);

        String viewName = userWebController.loginUser(login, session, model);

        assertEquals("redirect:/user/dashboard", viewName);
        verify(session, times(1)).setAttribute("user", user);
    }

    @Test
    public void loginUser_Failure() {
        Login login = new Login();
        CreateResponse response = new CreateResponse("Failure", 400, null);
        when(userService.loginUser(any(Login.class))).thenReturn(response);

        String viewName = userWebController.loginUser(login, session, model);

        assertEquals("login", viewName);
        verify(model, times(1)).addAttribute("error", "Failure");
    }

    @Test
    void logout() {

        String viewName = userWebController.logout(session);

        assertEquals("redirect:/user/login", viewName);
        verify(session, times(1)).invalidate();
    }

    @Test
    void showRegistrationPage() {
        String viewName = userWebController.showRegistrationPage(model);

        assertEquals("register", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testRegisterUser_Success() {
        User user = new User();

        String viewName = userWebController.registerUser(user, model);

        assertEquals("redirect:/user/login", viewName);
        verify(userService, times(1)).createUser(user);
    }

    @Test
    public void testRegisterUser_Failure() {
        User user = new User();
        doThrow(new UserOperationException("Error")).when(userService).createUser(any(User.class));

        String viewName = userWebController.registerUser(user, model);

        assertEquals("register", viewName);
        verify(model, times(1)).addAttribute("error", "Error");
    }

    @Test
    public void testShowDashboard_UserNotLoggedIn() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = userWebController.showDashboard(session, model);

        assertEquals("redirect:/user/login", viewName);
    }

    @Test
    public void testShowDashboard_Admin() {
        User user = new User();
        user.setUserRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(user);

        String viewName = userWebController.showDashboard(session, model);

        assertEquals("dashboard", viewName);
        verify(userService, times(1)).getAllUsers();
        verify(model, times(1)).addAttribute(eq("listOfUsers"), any());
    }

    @Test
    public void testShowDashboard_NonAdmin() {
        User user = new User();
        user.setUserRole("USER");
        when(session.getAttribute("user")).thenReturn(user);

        String viewName = userWebController.showDashboard(session, model);

        assertEquals("redirect:/user/products", viewName);
    }


    @Test
    public void testShowNewUserForm() {

        String viewName = userWebController.showNewUserForm(model);

        assertEquals("new_user", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testSaveUser_Success() {
        User user = new User();

        String viewName = userWebController.saveUser(user, model);

        assertEquals("redirect:/user/dashboard", viewName);
        verify(userService, times(1)).createUser(user);
    }

    @Test
    public void testSaveUser_Failure() {
        User user = new User();
        doThrow(new UserOperationException("Error")).when(userService).createUser(any(User.class));

        String viewName = userWebController.saveUser(user, model);

        assertEquals("new_user", viewName);
        verify(model, times(1)).addAttribute("error", "Error");
    }

    @Test
    public void testShowFormForUpdate() {
        User user = new User();
        when(userService.getUser(anyInt())).thenReturn(user);

        String viewName = userWebController.showFormForUpdate(1, model);

        assertEquals("update_user", viewName);
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User();

        String viewName = userWebController.updateUser(1, user, model);

        assertEquals("redirect:/user/dashboard", viewName);
        verify(userService, times(1)).updateUser(eq(1), eq(user));
    }

    @Test
    public void testUpdateUser_Failure() {
        User user = new User();
        doThrow(new UserOperationException("Error")).when(userService).updateUser(anyInt(), any(User.class));

        String viewName = userWebController.updateUser(1, user, model);

        assertEquals("update_user", viewName);
        verify(model, times(1)).addAttribute("error", "Error");
    }

    @Test
    public void testDeleteUser_Success() {

        String viewName = userWebController.deleteUser(1, model);

        assertEquals("redirect:/user/dashboard", viewName);
        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    public void testDeleteUser_Failure() {
        doThrow(new UserOperationException("Error")).when(userService).deleteUser(anyInt());

        String viewName = userWebController.deleteUser(1, model);

        assertEquals("redirect:/user/dashboard", viewName);
        verify(model, times(1)).addAttribute("error", "Error");
    }
}