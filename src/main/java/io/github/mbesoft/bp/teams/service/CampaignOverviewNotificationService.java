package io.github.mbesoft.bp.teams.service;

import io.github.mbesoft.bp.teams.card.CampaignOverviewCardFactory;
import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.model.CampaignOverviewNotification;
import org.springframework.stereotype.Service;

/**
 * Opt-in entry point for the multi-voting-unit campaign card.
 * TeamsNotificationService.sendCampaign() is unchanged.
 */
@Service
public class CampaignOverviewNotificationService {

    private final TeamsGatewayClient gatewayClient;
    private final CampaignOverviewCardFactory cardFactory;

    public CampaignOverviewNotificationService(TeamsGatewayClient gatewayClient,
                                               CampaignOverviewCardFactory cardFactory) {
        this.gatewayClient = gatewayClient;
        this.cardFactory = cardFactory;
    }

    public void sendCampaignOverview(CampaignOverviewNotification notification) {
        gatewayClient.sendJsonCard(cardFactory.create(notification));
    }
}
