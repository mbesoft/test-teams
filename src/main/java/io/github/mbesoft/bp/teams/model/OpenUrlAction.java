package io.github.mbesoft.bp.teams.model;

/** Action button containing a destination URL for the recipient. */
public final class OpenUrlAction {

    private final String type;
    private final String title;
    private final String url;

    public OpenUrlAction(String type, String title, String url) {
        this.type = type;
        this.title = title;
        this.url = url;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
}
