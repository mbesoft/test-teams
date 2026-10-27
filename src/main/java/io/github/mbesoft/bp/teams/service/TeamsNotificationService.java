package io.github.mbesoft.bp.teams.service;

import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import org.springframework.stereotype.Service;

/**
 * Business-facing entry point for Teams notifications.
 * Inject this service into an existing scheduler, use case or domain service.
 */
@Service
public class TeamsNotificationService {

    private final TeamsGatewayClient gatewayClient;
    private final AdaptiveCardFactory cardFactory;

    public TeamsNotificationService(TeamsGatewayClient gatewayClient,
                                    AdaptiveCardFactory cardFactory) {
        this.gatewayClient = gatewayClient;
        this.cardFactory = cardFactory;
    }

    /** Build the card and pass it to the authenticated gateway client. */
    public void sendCampaign(CampaignNotification notification) {
        gatewayClient.send(cardFactory.campaign(notification));
    }
}
