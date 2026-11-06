package io.github.mbesoft.bp.teams.model;

import java.util.List;
import java.util.Objects;

/** Existing campaign information, plus the voting units shown on its overview card. */
public final class CampaignOverviewNotification {

    private final CampaignNotification campaign;
    private final List<VotingUnitSummary> votingUnits;

    public CampaignOverviewNotification(CampaignNotification campaign,
                                        List<VotingUnitSummary> votingUnits) {
        this.campaign = Objects.requireNonNull(campaign, "Campaign is required");
        if (votingUnits == null || votingUnits.isEmpty()) {
            throw new IllegalArgumentException("At least one voting unit is required");
        }
        this.votingUnits = List.copyOf(votingUnits);
    }

    public CampaignNotification getCampaign() { return campaign; }
    public List<VotingUnitSummary> getVotingUnits() { return votingUnits; }
}
