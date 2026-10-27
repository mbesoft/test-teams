package io.github.mbesoft.bp.teams;

import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.service.TeamsNotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/** The gateway is mocked, so this test never opens a network connection. */
class TeamsNotificationServiceTest {

    @Test
    void delegatesAConstructedCardToTheGateway() {
        var gateway = mock(TeamsGatewayClient.class);
        var service = new TeamsNotificationService(gateway, new AdaptiveCardFactory());
        var notification = new CampaignNotification(
                "Campaign", "Open", "Action required",
                LocalDate.of(2026, 11, 2), URI.create("https://example.invalid/campaign/12"));

        service.sendCampaign(notification);

        var argument = ArgumentCaptor.forClass(AdaptiveCard.class);
        verify(gateway).send(argument.capture());
        assertThat(argument.getValue().body().get(0).text()).isEqualTo("Campaign");
        verifyNoMoreInteractions(gateway);
    }
}
