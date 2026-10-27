package io.github.mbesoft.bp.teams.model;

import java.util.List;

/**
 * Root Adaptive Card JSON object. The optional $schema URL is intentionally
 * omitted, so no external schema reference appears in the payload.
 */
public record AdaptiveCard(
        String type,
        String version,
        List<TextBlock> body,
        List<OpenUrlAction> actions
) {}
