package io.github.mbesoft.bp.teams.model;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TextBlock(String type, String id, String text, String spacing, String size, String weight, String color, Boolean separator, boolean wrap) {}
