package dev.raspberrykan.cveboard.repository;

import dev.raspberrykan.cveboard.models.dao.ProjectDao;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectDao, UUID> {
    List<ProjectDao> findAllByOrderByCreatedAtDesc();

    @Override
    @NullMarked
    @EntityGraph(attributePaths = {"dependencies", "dependencies.component"})
    Optional<ProjectDao> findById(UUID id);
}
