package io.github.mbesoft.bp.teams.model;

/** A card button that opens a business URL in the user's browser. */
public record OpenUrlAction(String type, String title, String url) {}
