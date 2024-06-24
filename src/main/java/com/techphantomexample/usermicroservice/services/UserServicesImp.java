package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.controller.CreateResponse;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.entity.User;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


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
    public CreateResponse loginUser(Login login) {
        if (login.getUserEmail() == null || login.getUserEmail().isEmpty() ||
                login.getUserPassword() == null || login.getUserPassword().isEmpty()) {
            log.error("All fields are required for login");
            return new CreateResponse("All fields are required", 400, null); // 400 for Bad Request
        }
        User user = userRepository.findByUserEmail(login.getUserEmail());
        if (user != null) {
            String password = login.getUserPassword();
            String encodedPassword = user.getUserPassword();
            boolean isPwdRight = BCrypt.checkpw(password, encodedPassword);
            if (isPwdRight) {
                log.info("User logged in successfully: {}", user.getUserEmail());
                return new CreateResponse("Login Success", 200, user);
            } else {
                log.error("Incorrect password for user: {}", user.getUserEmail());
                return new CreateResponse("Password does not match", 401, user);
            }
        } else {
            log.error("No user found with email: {}", login.getUserEmail());
            return new CreateResponse("Email does not exist", 401, null);
        }
    }

    @Override
    public String createUser(User user) {
        UserOperationException.validateUser(user, userRepository);
        user.setUserPassword(BCrypt.hashpw(user.getUserPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return "User Created successfully";
    }

    @Override
    public String updateUser(int userId, User newUserDetails) {
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
    }

    @Override
    public String deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserOperationException("User with ID " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
        return "User Deleted Successfully";
    }

    @Override
    public User getUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserOperationException("User with ID " + userId + " does not exist");
        }
        return userRepository.findById(userId).get();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}
