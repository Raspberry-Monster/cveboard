package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.ComponentDao;

public record ComponentResponse(Long id, String name, String description) {
    public static ComponentResponse from(ComponentDao component) {
        return new ComponentResponse(component.getId(), component.getName(), component.getDescription());
    }
}
