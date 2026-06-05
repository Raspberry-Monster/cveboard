package dev.raspberrykan.cveboard.services;

import dev.raspberrykan.cveboard.models.dao.UserDao;
import dev.raspberrykan.cveboard.models.dto.responses.UserResponse;
import dev.raspberrykan.cveboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserResponse createUser(String userName, String nickName, String password) {
        requireText(userName, "User name cannot be blank.");
        requireText(nickName, "Nick name cannot be blank.");
        requireText(password, "Password cannot be blank.");
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("User name already exists.");
        }

        UserDao user = new UserDao();
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setPassword(passwordHasher.hash(password));
        user.setRegisterDate(new Date());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse recordLogin(UUID id) {
        UserDao user = getUserDao(id);
        user.setLastLoginDate(new Date());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return UserResponse.from(getUserDao(id));
    }

    private UserDao getUserDao(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Transactional
    public UserResponse updateInfo(UUID id, String nickName) {
        requireText(nickName, "Nick name cannot be blank.");
        UserDao user = getUserDao(id);
        user.setNickName(nickName);
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(UUID id, String oldPassword, String newPassword) {
        requireText(newPassword, "New password cannot be blank.");
        UserDao user = getUserDao(id);
        if (!passwordHasher.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        user.setPassword(passwordHasher.hash(newPassword));
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
