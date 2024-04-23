package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.model.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServicesImp implements UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserServicesImp.class);
    @Autowired
    UserRepository userRepository;

    public UserServicesImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public String createUser(User user) {
        try {
            UserOperationException.validateUser(user , userRepository);
            user.setUserPassword(BCrypt.hashpw(user.getUserPassword(), BCrypt.gensalt()));
            userRepository.save(user);
            return "User Created successfully";
        } catch (UserOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new UserOperationException("Error creating user", e);
        }
    }


    @Override
    public String updateUser(int userId, User newUserDetails) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserOperationException("User with ID " + userId + " does not exist");
            }

            User existingUser = userRepository.findById(userId).get();

            UserOperationException.validateUpdatedUser(newUserDetails);

            existingUser.setUserFullName(newUserDetails.getUserFullName());
            existingUser.setUserEmail(newUserDetails.getUserEmail());
            existingUser.setUserPassword(BCrypt.hashpw(newUserDetails.getUserPassword(), BCrypt.gensalt()));
            existingUser.setUserRole(newUserDetails.getUserRole());

            userRepository.save(existingUser);
            return "User Updated Successfully";
        } catch (UserOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new UserOperationException("Error updating user", e);
        }
    }

    @Override
    public String deleteUser(int userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserOperationException("User with ID " + userId + " does not exist");
            }
            userRepository.deleteById(userId);
            return "User Deleted Successfully";
        } catch (UserOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new UserOperationException("Error deleting user", e);
        }
    }

    @Override
    public User getUser(int userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return null; // Return null if user does not exist
            }
            return userRepository.findById(userId).get();
        } catch (Exception e) {
            throw new UserOperationException("Error retrieving user", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new UserOperationException("Error retrieving users", e);
        }
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
