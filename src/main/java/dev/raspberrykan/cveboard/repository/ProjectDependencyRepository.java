package dev.raspberrykan.cveboard.repository;

import dev.raspberrykan.cveboard.models.dao.ProjectDao;
import dev.raspberrykan.cveboard.models.dao.ProjectDependencyDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectDependencyRepository extends JpaRepository<ProjectDependencyDao, Long> {
    Optional<ProjectDependencyDao> findByIdAndProject(Long id, ProjectDao project);
}
