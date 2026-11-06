package io.github.mbesoft.bp.teams.model;

/** Display statuses for a voting unit in a campaign overview. */
public enum VotingUnitStatus {
    VOTING_OPEN("VOTING OPEN", "Good", null),
    NOT_STARTED("NOT STARTED", "Warning", "Voting has not started for this unit."),
    COMPLETED("COMPLETED", "Accent", "Voting is complete for this unit."),
    CLOSED("CLOSED", "Warning", "Voting is closed for this unit.");

    private final String label;
    private final String color;
    private final String description;

    VotingUnitStatus(String label, String color, String description) {
        this.label = label;
        this.color = color;
        this.description = description;
    }

    public String getLabel() { return label; }
    public String getColor() { return color; }
    public String getDescription() { return description; }
}
