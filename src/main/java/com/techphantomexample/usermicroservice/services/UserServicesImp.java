package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServicesImp implements UserService
{
    @Autowired
    UserRepository userRepository;

    public UserServicesImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public String createUser(User user) {
        String userFullName = user.getUserFullName();
        String userEmail = user.getUserEmail();
        String userPassword = user.getUserPassword();
        String userRole = user.getUserRole();

        if (StringUtils.isBlank(userFullName) ||StringUtils.isBlank(userEmail) || StringUtils.isBlank(userPassword) || StringUtils.isBlank(userRole)) {
            return "All fields are required";
        }

        if (!isValidEmail(userEmail)) {
            return "Invalid email address";
        }

        if (existsByEmail(userEmail)) {
            return "User with provided Email ID exists";
        }

        if (!isValidPassword(userPassword)) {
            return "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit";
        }

        String hashedPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());
        user.setUserPassword(hashedPassword);

        if (!isValidUserRole(userRole)) {
            return "User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER";
        }

        userRepository.save(user);
        return "User Created successfully";
    }

    @Override
    public String updateUser(int userId, User newUserDetails) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            existingUser.setUserFullName(newUserDetails.getUserFullName());

            String newEmail = newUserDetails.getUserEmail();
            if (newEmail != null && !isValidEmail(newEmail)) {
                return "Invalid email address";
            }
            existingUser.setUserEmail(newEmail);

            String newPassword = newUserDetails.getUserPassword();
            if (newPassword != null && !isValidPassword(newPassword)) {
                return "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one digit";
            }
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            existingUser.setUserPassword(hashedPassword);

            String newUserRole = newUserDetails.getUserRole();
            if (newUserRole != null && !isValidUserRole(newUserRole)) {
                return "User role should be one among: ADMIN, SUPERVISOR, BUYER, SELLER";
            }
            existingUser.setUserRole(newUserRole);


            userRepository.save(existingUser);

            return "User Updated Successfully";
        } else {

            return "";
        }
    }

    @Override
    public String deleteUser(int userId) {
        if (userRepository.existsById(userId))
        {
            userRepository.deleteById(userId);
            return "User Deleted Successfully";
        }
        return "User Not Found";
    }

    @Override
    public User getUser(int userId) {
        if (userRepository.existsById(userId))
        {
            return userRepository.findById(userId).get();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {

        userRepository.findAll();
        return userRepository.findAll();
    }

    private boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    private boolean existsByEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[A-Z])(?=.*\\d).+$");
    }

    private boolean isValidUserRole(String userRole) {
        if (userRole == null) {
            return false;
        }
        String userRoleString = userRole.toString().toUpperCase(); // Convert user role to uppercase
        List<String> validRoles = Arrays.asList("ADMIN", "SUPERVISOR", "BUYER", "SELLER");
        return validRoles.contains(userRoleString);
    }

}
