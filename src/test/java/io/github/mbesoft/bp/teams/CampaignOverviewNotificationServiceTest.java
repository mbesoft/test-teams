package io.github.mbesoft.bp.teams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mbesoft.bp.teams.card.CampaignOverviewCardFactory;
import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.model.CampaignOverviewNotification;
import io.github.mbesoft.bp.teams.model.VotingUnitStatus;
import io.github.mbesoft.bp.teams.model.VotingUnitSummary;
import io.github.mbesoft.bp.teams.service.CampaignOverviewNotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CampaignOverviewNotificationServiceTest {

    @Test
    void delegatesRenderedCardToTheExistingGateway() {
        TeamsGatewayClient gateway = mock(TeamsGatewayClient.class);
        CampaignOverviewNotificationService service =
                new CampaignOverviewNotificationService(
                        gateway, new CampaignOverviewCardFactory(new ObjectMapper()));

        CampaignNotification campaign = new CampaignNotification(
                "Campaign 2026", "Voting Open", "Please vote",
                LocalDate.of(2026, 11, 30),
                URI.create("https://example.invalid/campaign/42"));
        service.sendCampaignOverview(new CampaignOverviewNotification(
                campaign, List.of(new VotingUnitSummary(
                        "FX", VotingUnitStatus.VOTING_OPEN, 75,
                        URI.create("https://example.invalid/campaign/42/fx")))));

        ArgumentCaptor<JsonNode> argument = ArgumentCaptor.forClass(JsonNode.class);
        verify(gateway).sendJsonCard(argument.capture());
        assertThat(argument.getValue().at("/body/5/items/3/columns/0/items/0/text").asText())
                .isEqualTo("75% done");
        assertThat(argument.getValue().at("/body/5/items/3/columns/1/items/0/text").asText())
                .isEqualTo("25% pending");
        verifyNoMoreInteractions(gateway);
    }
}
