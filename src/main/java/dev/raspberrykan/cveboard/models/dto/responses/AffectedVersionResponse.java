package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.AffectedVersionDao;

public record AffectedVersionResponse(String versionExpression) {
    public static AffectedVersionResponse from(AffectedVersionDao affectedVersion) {
        return new AffectedVersionResponse(
                affectedVersion.getVersionExpression());
    }
}
