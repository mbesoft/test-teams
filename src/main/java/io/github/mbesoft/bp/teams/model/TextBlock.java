package io.github.mbesoft.bp.teams.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Text element in an Adaptive Card.
 * Optional null properties are omitted from JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TextBlock {

    private final String type;
    private final String id;
    private final String text;
    private final String spacing;
    private final String size;
    private final String weight;
    private final String color;
    private final Boolean separator;
    private final boolean wrap;

    public TextBlock(String type, String id, String text,
                     String spacing, String size, String weight,
                     String color, Boolean separator, boolean wrap) {
        this.type = type;
        this.id = id;
        this.text = text;
        this.spacing = spacing;
        this.size = size;
        this.weight = weight;
        this.color = color;
        this.separator = separator;
        this.wrap = wrap;
    }

    public String getType() { return type; }
    public String getId() { return id; }
    public String getText() { return text; }
    public String getSpacing() { return spacing; }
    public String getSize() { return size; }
    public String getWeight() { return weight; }
    public String getColor() { return color; }
    public Boolean getSeparator() { return separator; }
    public boolean isWrap() { return wrap; }
}
