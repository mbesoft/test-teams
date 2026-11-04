package io.github.mbesoft.bp.teams.model;

import java.net.URI;
import java.time.LocalDate;

/**
 * Validated business data for one campaign notification.
 * The payload can be built without involving HTTP or any external service.
 */
public final class CampaignNotification {

    private final String name;
    private final String stage;
    private final String message;
    private final LocalDate deadline;
    private final URI deepLink;

    public CampaignNotification(String name, String stage, String message,
                                LocalDate deadline, URI deepLink) {
        if (name == null || name.isBlank()
                || stage == null || stage.isBlank()
                || message == null || message.isBlank()
                || deadline == null) {
            throw new IllegalArgumentException(
                    "Campaign name, stage, message and deadline are required");
        }
        if (deepLink == null || !"https".equalsIgnoreCase(deepLink.getScheme())
                || deepLink.getHost() == null) {
            throw new IllegalArgumentException(
                    "The campaign link must be an absolute HTTPS URL");
        }
        this.name = name;
        this.stage = stage;
        this.message = message;
        this.deadline = deadline;
        this.deepLink = deepLink;
    }

    public String getName() { return name; }
    public String getStage() { return stage; }
    public String getMessage() { return message; }
    public LocalDate getDeadline() { return deadline; }
    public URI getDeepLink() { return deepLink; }
}
