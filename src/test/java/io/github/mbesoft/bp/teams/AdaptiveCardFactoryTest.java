package io.github.mbesoft.bp.teams;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** No Spring context or network requests are started in this unit test. */
class AdaptiveCardFactoryTest {

    private final AdaptiveCardFactory factory = new AdaptiveCardFactory();

    @Test
    void createsExpectedCardJsonWithJava11Beans() throws Exception {
        CampaignNotification notification = new CampaignNotification(
                "Campaign 2026",
                "Open",
                "Please complete your review",
                LocalDate.of(2026, 10, 31),
                URI.create("https://example.invalid/campaign/42"));

        AdaptiveCard card = factory.campaign(notification);
        String json = new ObjectMapper().writeValueAsString(card);

        assertThat(card.getType()).isEqualTo("AdaptiveCard");
        assertThat(card.getVersion()).isEqualTo("1.0");
        assertThat(card.getBody()).hasSize(4);
        assertThat(card.getBody().get(3).getText()).isEqualTo("Due date: 2026-10-31");
        assertThat(card.getActions().get(0).getTitle()).isEqualTo("OPEN IN BP");
        assertThat(json).contains("\"type\":\"AdaptiveCard\"");
        assertThat(json).doesNotContain("$schema");
        assertThat(json).contains("Action.OpenUrl");
    }
}
