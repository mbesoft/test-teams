package io.github.mbesoft.bp.teams.service;
import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import org.springframework.stereotype.Service;
@Service
public class TeamsNotificationService {
 private final TeamsGatewayClient client;
 private final AdaptiveCardFactory cards;
 public TeamsNotificationService(TeamsGatewayClient client, AdaptiveCardFactory cards) {
  this.client=client; this.cards=cards;
 }
 public void sendCampaign(CampaignNotification notification) { client.send(cards.campaign(notification)); }
}
