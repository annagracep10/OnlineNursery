package com.techphantomexample.usermicroservice.services;
import com.techphantomexample.usermicroservice.entity.Cart;
import com.techphantomexample.usermicroservice.exception.UserOperationException;
import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.repository.CartRepository;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import com.techphantomexample.usermicroservice.validator.PasswordValidator;
import com.techphantomexample.usermicroservice.validator.UserValidator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class UserServicesImp implements UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserServicesImp.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    PasswordValidator passwordValidator;

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
        existingUser.setPhone(newUserDetails.getPhone());
        existingUser.setAddress(newUserDetails.getAddress());
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

    public int getUserIdByEmail(String email) {
        return userRepository.findByUserEmail(email).getUserId();
    }

    @Override
    public String changePassword(int userId, String currentPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "User not found.";
        }

        if (!BCrypt.checkpw(currentPassword, user.getUserPassword())) {
            return "Current password is incorrect.";
        }

        PasswordValidator.isValidPassword(newPassword);

        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        user.setUserPassword(hashedNewPassword);
        userRepository.save(user);

        return "Password changed successfully.";
    }


}
