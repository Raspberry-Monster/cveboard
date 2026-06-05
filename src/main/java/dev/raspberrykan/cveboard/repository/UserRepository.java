package dev.raspberrykan.cveboard.repository;

import dev.raspberrykan.cveboard.models.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDao, UUID> {
    Optional<UserDao> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
