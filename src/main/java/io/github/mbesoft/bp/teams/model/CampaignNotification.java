package io.github.mbesoft.bp.teams.model;
import java.net.URI;
import java.time.LocalDate;
public record CampaignNotification(String name, String stage, String message, LocalDate deadline, URI deepLink) {
 public CampaignNotification {
  if (name == null || name.isBlank() || stage == null || stage.isBlank() || message == null || message.isBlank() || deadline == null) throw new IllegalArgumentException("Données de campagne manquantes");
  if (deepLink == null || !"https".equalsIgnoreCase(deepLink.getScheme()) || deepLink.getHost() == null) throw new IllegalArgumentException("Lien HTTPS requis");
 }
}
