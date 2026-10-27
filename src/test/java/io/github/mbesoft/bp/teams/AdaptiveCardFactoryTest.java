package io.github.mbesoft.bp.teams;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Pure unit tests: no application context, credentials or HTTP requests. */
class AdaptiveCardFactoryTest {

    private final AdaptiveCardFactory factory = new AdaptiveCardFactory();

    @Test
    void createsValidCardDataWithoutExternalSchema() throws Exception {
        var notification = new CampaignNotification(
                "Campaign 2026",
                "Open",
                "Please complete your review",
                LocalDate.of(2026, 10, 31),
                URI.create("https://example.invalid/campaign/42"));

        var card = factory.campaign(notification);
        String json = new ObjectMapper().writeValueAsString(card);

        assertThat(card.type()).isEqualTo("AdaptiveCard");
        assertThat(card.version()).isEqualTo("1.0");
        assertThat(card.body()).hasSize(4);
        assertThat(card.body().get(3).text()).isEqualTo("Due date: 2026-10-31");
        assertThat(card.actions().get(0).title()).isEqualTo("OPEN IN BP");
        assertThat(json).doesNotContain("$schema");
        assertThat(json).contains("Action.OpenUrl");
    }
}
