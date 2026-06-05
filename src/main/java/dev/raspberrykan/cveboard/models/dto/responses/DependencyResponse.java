package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.ProjectDependencyDao;

public record DependencyResponse(Long id, Long componentId, String name, String version) {
    public static DependencyResponse from(ProjectDependencyDao dependency) {
        return new DependencyResponse(
                dependency.getId(),
                dependency.getComponent().getId(),
                dependency.getComponent().getName(),
                dependency.getVersion()
        );
    }
}
