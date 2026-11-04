package io.github.mbesoft.bp.teams.model;

import java.util.List;

/**
 * Root Adaptive Card payload serialized by Jackson.
 * The optional schema property is not included in the current repository version.
 */
public final class AdaptiveCard {

    private final String type;
    private final String version;
    private final List<TextBlock> body;
    private final List<OpenUrlAction> actions;

    public AdaptiveCard(String type, String version,
                        List<TextBlock> body, List<OpenUrlAction> actions) {
        this.type = type;
        this.version = version;
        this.body = body;
        this.actions = actions;
    }

    public String getType() { return type; }
    public String getVersion() { return version; }
    public List<TextBlock> getBody() { return body; }
    public List<OpenUrlAction> getActions() { return actions; }
}
