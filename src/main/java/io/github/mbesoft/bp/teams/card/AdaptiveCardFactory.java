package io.github.mbesoft.bp.teams.card;

import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.model.OpenUrlAction;
import io.github.mbesoft.bp.teams.model.TextBlock;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds typed Adaptive Card payloads without an external card SDK.
 * Standard JavaBeans replace Java records to support Java 11.
 */
@Component
public class AdaptiveCardFactory {

    public AdaptiveCard campaign(CampaignNotification notification) {
        return new AdaptiveCard(
                "AdaptiveCard",
                "1.0",
                List.of(
                        new TextBlock("TextBlock", "Title", notification.getName(),
                                "Medium", "ExtraLarge", "Bolder", "Accent", null, true),
                        new TextBlock("TextBlock", "Stage",
                                "Stage: " + notification.getStage(),
                                null, null, null, null, true, true),
                        new TextBlock("TextBlock", "Message", notification.getMessage(),
                                null, null, null, null, null, true),
                        new TextBlock("TextBlock", "Deadline",
                                "Due date: " + notification.getDeadline()
                                        .format(DateTimeFormatter.ISO_LOCAL_DATE),
                                null, null, "Bolder", null, null, true)
                ),
                List.of(new OpenUrlAction(
                        "Action.OpenUrl",
                        "OPEN IN BP",
                        notification.getDeepLink().toString()
                ))
        );
    }
}
