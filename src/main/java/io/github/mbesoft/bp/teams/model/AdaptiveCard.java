package io.github.mbesoft.bp.teams.model;
import java.util.List;
public record AdaptiveCard(String type, String version, List<TextBlock> body, List<OpenUrlAction> actions) {}
