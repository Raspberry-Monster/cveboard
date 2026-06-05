package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.ProjectDao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String description, Date createdAt, List<DependencyResponse> dependencies) {
    public static ProjectResponse from(ProjectDao project) {
        List<DependencyResponse> dependencies = null;
        if (project.getDependencies() != null && !project.getDependencies().isEmpty()) {
            dependencies = project.getDependencies().stream()
                    .map(DependencyResponse::from)
                    .toList();
        }
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt(), dependencies);
    }
}
