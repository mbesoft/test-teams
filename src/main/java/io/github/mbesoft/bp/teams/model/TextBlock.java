package io.github.mbesoft.bp.teams.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A text element in the Adaptive Card body.
 * Optional fields are omitted from the serialized JSON when null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TextBlock(
        String type,
        String id,
        String text,
        String spacing,
        String size,
        String weight,
        String color,
        Boolean separator,
        boolean wrap
) {}
