package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.ProjectDao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ListProjectResponse(UUID id, String name, String description, Date createdAt) {
    public static ListProjectResponse from(ProjectDao project) {
        return new ListProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt());
    }
}
