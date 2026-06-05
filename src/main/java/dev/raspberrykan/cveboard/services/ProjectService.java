package dev.raspberrykan.cveboard.services;

import dev.raspberrykan.cveboard.models.dao.ComponentDao;
import dev.raspberrykan.cveboard.models.dao.ProjectDao;
import dev.raspberrykan.cveboard.models.dao.ProjectDependencyDao;
import dev.raspberrykan.cveboard.models.dao.UserDao;
import dev.raspberrykan.cveboard.models.dto.responses.*;
import dev.raspberrykan.cveboard.repository.ComponentRepository;
import dev.raspberrykan.cveboard.repository.ProjectDependencyRepository;
import dev.raspberrykan.cveboard.repository.ProjectRepository;
import dev.raspberrykan.cveboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;
    private final ProjectDependencyRepository projectDependencyRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, ComponentRepository componentRepository, ProjectDependencyRepository projectDependencyRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
        this.projectDependencyRepository = projectDependencyRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse createProject(UUID ownerId, String name, String description) {
        requireText(name, "Project name cannot be blank.");
        ProjectDao project = new ProjectDao();
        project.setOwner(getUserDao(ownerId));
        project.setName(name);
        project.setDescription(description);
        project.setCreatedAt(new Date());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ListProjectResponse> listProjects() {
        return projectRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ListProjectResponse::from)
                .toList();
    }

    @Transactional
    public ProjectResponse updateProject(UUID projectId, String name, String description) {
        requireText(name, "Project name cannot be blank.");
        ProjectDao project = getProjectDao(projectId);
        project.setName(name);
        project.setDescription(description);
        return ProjectResponse.from(project);
    }

    public ComponentResponse createDependency(String name, String description) {
        requireText(name, "Dependency name cannot be blank.");
        ComponentDao component = new ComponentDao();
        component.setName(name);
        component.setDescription(description);
        return ComponentResponse.from(componentRepository.save(component));
    }

    public void deleteDependency(Long id) {
        var component = componentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Dependency not found."));;
        componentRepository.delete(component);
    }

    @Transactional
    public DependencyResponse addDependency(UUID projectId, Long id, String version) {
        requireText(version, "Dependency version cannot be blank.");
        ProjectDao project = getProjectDao(projectId);
        var component = componentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Dependency not found."));;
        ProjectDependencyDao dependency = new ProjectDependencyDao();
        dependency.setProject(project);
        dependency.setComponent(component);
        dependency.setVersion(version);
        if (project.getDependencies() != null) {
            project.getDependencies().add(dependency);
        }
        return DependencyResponse.from(projectDependencyRepository.save(dependency));
    }

    @Transactional
    public DependencyResponse updateDependency(UUID projectId, Long dependencyId, String version) {
        ProjectDao project = getProjectDao(projectId);
        ProjectDependencyDao dependency = projectDependencyRepository.findByIdAndProject(dependencyId, project)
                .orElseThrow(() -> new IllegalArgumentException("Dependency not found."));
        dependency.setVersion(version);
        return DependencyResponse.from(dependency);
    }

    @Transactional
    public void removeDependency(UUID projectId, Long dependencyId) {
        ProjectDao project = getProjectDao(projectId);
        ProjectDependencyDao dependency = projectDependencyRepository.findByIdAndProject(dependencyId, project)
                .orElseThrow(() -> new IllegalArgumentException("Dependency not found."));
        if (project.getDependencies() != null) {
            project.getDependencies().remove(dependency);
        }
        projectDependencyRepository.delete(dependency);
    }

    public List<ComponentResponse> listDependencies(){
        return componentRepository.findAllByOrderByIdDesc().stream()
                .map(ComponentResponse::from)
                .toList();
    }

    public void deleteProject(UUID projectId) {
        ProjectDao project = getProjectDao(projectId);
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId) {
        return ProjectResponse.from(getProjectDao(projectId));
    }

    private ProjectDao getProjectDao(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    private UserDao getUserDao(UUID ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
