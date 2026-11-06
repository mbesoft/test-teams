package io.github.mbesoft.bp.teams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mbesoft.bp.teams.card.CampaignOverviewCardFactory;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.model.CampaignOverviewNotification;
import io.github.mbesoft.bp.teams.model.VotingUnitStatus;
import io.github.mbesoft.bp.teams.model.VotingUnitSummary;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignOverviewCardFactoryTest {

    private final CampaignOverviewCardFactory factory =
            new CampaignOverviewCardFactory(new ObjectMapper());

    @Test
    void rendersAllUnitsAndOnlyOpenUnitsHaveProgress() {
        CampaignOverviewNotification overview = new CampaignOverviewNotification(
                campaign(),
                List.of(
                        unit("FX Trading Desk", VotingUnitStatus.VOTING_OPEN, 70, "fx"),
                        unit("Equities Desk", VotingUnitStatus.VOTING_OPEN, 40, "equities"),
                        unit("Credit Desk", VotingUnitStatus.COMPLETED, null, "credit")
                ));

        JsonNode card = factory.create(overview);

        assertThat(card.path("type").asText()).isEqualTo("AdaptiveCard");
        assertThat(card.path("version").asText()).isEqualTo("1.2");
        assertThat(card.path("body").size()).isEqualTo(9);
        assertThat(card.at("/body/1/text").asText()).isEqualTo("Broker Evaluation Campaign");
        assertThat(card.at("/body/3/columns/0/items/1/text").asText()).isEqualTo("VOTING OPEN");
        assertThat(card.at("/body/3/columns/1/items/1/text").asText()).isEqualTo("30 Nov 2026");
        assertThat(card.at("/body/4/text").asText()).isEqualTo("VOTING UNITS (3)");

        assertThat(card.at("/body/5/items/0/columns/0/items/0/text").asText())
                .isEqualTo("FX Trading Desk");
        assertThat(card.at("/body/5/items/3/columns/0/items/0/text").asText())
                .isEqualTo("70% done");
        assertThat(card.at("/body/5/items/3/columns/1/items/0/text").asText())
                .isEqualTo("30% pending");
        assertThat(card.at("/body/5/items/2/inlines/0/text").asText())
                .isEqualTo("███████");
        assertThat(card.at("/body/6/items/3/columns/0/items/0/text").asText())
                .isEqualTo("40% done");
        assertThat(card.at("/body/6/items/3/columns/1/items/0/text").asText())
                .isEqualTo("60% pending");

        assertThat(card.at("/body/7/items/0/columns/1/items/0/text").asText())
                .isEqualTo("COMPLETED");
        assertThat(card.at("/body/7/items/1/text").asText())
                .isEqualTo("Voting is complete for this unit.");
        assertThat(card.at("/body/7").toString()).doesNotContain("% done", "% pending", "VOTE COMPLETION");

        assertThat(card.at("/body/5/items/4/actions/0/url").asText())
                .isEqualTo("https://example.invalid/campaign/fx");
        assertThat(card.at("/body/6/items/4/actions/0/url").asText())
                .isEqualTo("https://example.invalid/campaign/equities");
        assertThat(card.at("/body/7/items/2/actions/0/url").asText())
                .isEqualTo("https://example.invalid/campaign/credit");
        assertThat(card.at("/actions/0/url").asText())
                .isEqualTo("https://example.invalid/campaign");
    }

    @Test
    void rendersDifferentUnitCountsWithoutMutatingTheTemplate() {
        JsonNode first = factory.create(new CampaignOverviewNotification(
                campaign(), List.of(unit("Desk A", VotingUnitStatus.VOTING_OPEN, 0, "a"))));
        JsonNode second = factory.create(new CampaignOverviewNotification(
                campaign(), List.of(unit("Desk B", VotingUnitStatus.NOT_STARTED, null, "b"))));

        assertThat(first.path("body").size()).isEqualTo(7);
        assertThat(first.at("/body/5/items/3/columns/0/items/0/text").asText())
                .isEqualTo("0% done");
        assertThat(first.at("/body/5/items/3/columns/1/items/0/text").asText())
                .isEqualTo("100% pending");

        assertThat(second.at("/body/4/text").asText()).isEqualTo("VOTING UNITS (1)");
        assertThat(second.at("/body/5/items/0/columns/0/items/0/text").asText())
                .isEqualTo("Desk B");
        assertThat(second.at("/body/5/items/1/text").asText())
                .isEqualTo("Voting has not started for this unit.");
        assertThat(second.at("/body/5").toString()).doesNotContain("% done", "% pending");
    }

    @Test
    void rejectsInvalidOpenVotingUnitProgress() {
        assertThatThrownBy(() -> unit("FX", VotingUnitStatus.VOTING_OPEN, null, "fx"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> unit("FX", VotingUnitStatus.VOTING_OPEN, 101, "fx"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private CampaignNotification campaign() {
        return new CampaignNotification("Broker Evaluation Campaign", "Voting Open",
                "Review outstanding broker evaluations.", LocalDate.of(2026, 11, 30),
                URI.create("https://example.invalid/campaign"));
    }

    private VotingUnitSummary unit(String name, VotingUnitStatus status,
                                   Integer done, String slug) {
        return new VotingUnitSummary(name, status, done,
                URI.create("https://example.invalid/campaign/" + slug));
    }
}
