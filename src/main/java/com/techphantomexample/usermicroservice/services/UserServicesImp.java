package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.model.Login;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.validator.UserValidator;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@AllArgsConstructor
@Service
public class UserServicesImp implements UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserServicesImp.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Override
    public CreateResponse loginUser(Login login)  {
        if ( login.getUserEmail().isEmpty() || login.getUserPassword().isEmpty()) {
            log.error("All fields are required for login");
            return new CreateResponse("All fields are required", 400, null);
        }
        else{
            UserEntity user = userRepository.findByUserEmail(login.getUserEmail());
            if (user != null) {
                String password = login.getUserPassword();
                String encodedPassword = user.getUserPassword();
                boolean isPwdRight = BCrypt.checkpw(password, encodedPassword);
                if (isPwdRight) {
                    log.info("User logged in successfully: {}", user.getUserEmail());
                    return new CreateResponse("Login Success", 200, user);
                } else {
                    log.error("Incorrect password for user: {}", user.getUserEmail());
                    return new CreateResponse("Password does not match", 401, null);
                }
            } else {
                log.error("No user found with email: {}", login.getUserEmail());
                return new CreateResponse("Email does not exist", 401, null);
            }
        }

    }

    @Override
    public String createUser(UserEntity user) {
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            throw new UserOperationException("User with provided email ID exists");
        }
        UserValidator.validateUser(user, userRepository);
        user.setUserPassword(BCrypt.hashpw(user.getUserPassword(), BCrypt.gensalt()));
        userRepository.save(user);
        return "User Created successfully";
    }

    @Override
    public String updateUser(int userId, UserEntity newUserDetails) {
        if (!userRepository.existsById(userId)) {
            throw new UserOperationException("User with ID " + userId + " does not exist");
        }
        UserEntity existingUser = userRepository.findById(userId).get();
        UserValidator.validateUser(newUserDetails,userRepository);
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
    public UserEntity getUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserOperationException("User with ID " + userId + " does not exist");
        }
        return userRepository.findById(userId).get();
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Cart getCartByUserId(int userId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        if (cart == null) {
            cart = new Cart();
            UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserOperationException("User not found with id: " + userId));
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }


}
