package io.github.mbesoft.bp.teams.model;

import java.net.URI;
import java.util.Objects;

/**
 * One voting unit displayed in a campaign overview.
 * Pending percentage is derived from done percentage (100 - done).
 * Only VOTING_OPEN units display these percentages.
 */
public final class VotingUnitSummary {

    private final String name;
    private final VotingUnitStatus status;
    private final Integer donePercentage;
    private final URI deepLink;

    public VotingUnitSummary(String name, VotingUnitStatus status,
                             Integer donePercentage, URI deepLink) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Voting unit name is required");
        }
        this.status = Objects.requireNonNull(status, "Voting unit status is required");
        if (status == VotingUnitStatus.VOTING_OPEN
                && (donePercentage == null || donePercentage < 0 || donePercentage > 100)) {
            throw new IllegalArgumentException(
                    "Open voting units require a done percentage between 0 and 100");
        }
        if (deepLink == null || !"https".equalsIgnoreCase(deepLink.getScheme())
                || deepLink.getHost() == null) {
            throw new IllegalArgumentException("Voting unit link must be an absolute HTTPS URL");
        }
        this.name = name;
        this.donePercentage = donePercentage;
        this.deepLink = deepLink;
    }

    public String getName() { return name; }
    public VotingUnitStatus getStatus() { return status; }
    public Integer getDonePercentage() { return donePercentage; }
    public int getPendingPercentage() {
        if (status != VotingUnitStatus.VOTING_OPEN) {
            throw new IllegalStateException("Progress is only available for open voting units");
        }
        return 100 - donePercentage;
    }
    public URI getDeepLink() { return deepLink; }
}
