package dev.raspberrykan.cveboard.repository;

import dev.raspberrykan.cveboard.models.dao.ComponentDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends JpaRepository<ComponentDao, Long> {
    List<ComponentDao> findAllByOrderByIdDesc();
}
