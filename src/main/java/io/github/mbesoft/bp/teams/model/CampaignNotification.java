package io.github.mbesoft.bp.teams.model;

import java.net.URI;
import java.time.LocalDate;

/**
 * Business data passed to the notification service.
 * No gateway-specific JSON fields are exposed to callers.
 */
public record CampaignNotification(
        String name,
        String stage,
        String message,
        LocalDate deadline,
        URI deepLink
) {
    public CampaignNotification {
        if (name == null || name.isBlank()
                || stage == null || stage.isBlank()
                || message == null || message.isBlank()
                || deadline == null) {
            throw new IllegalArgumentException("Campaign name, stage, message and deadline are required");
        }
        // Only absolute HTTPS links are accepted for the card action.
        if (deepLink == null || !"https".equalsIgnoreCase(deepLink.getScheme())
                || deepLink.getHost() == null) {
            throw new IllegalArgumentException("The campaign link must be an absolute HTTPS URL");
        }
    }
}
