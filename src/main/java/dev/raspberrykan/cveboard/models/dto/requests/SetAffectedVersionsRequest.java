package dev.raspberrykan.cveboard.models.dto.requests;

import java.util.List;

public record SetAffectedVersionsRequest(List<AffectedVersionRequest> affectedVersions) {
}
