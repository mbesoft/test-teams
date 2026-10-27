package io.github.mbesoft.bp.teams.runner;

import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.service.TeamsNotificationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDate;

/**
 * Sends one sample campaign notification when the local profile starts.
 *
 * IMPORTANT: This runner executes on every startup with the "local" profile.
 * A real request is attempted only when bp.teams.enabled=true.
 * The destination URL for the card button below is intentionally fictitious.
 */
@Component
@Profile("local")
public class LocalTeamsRunner implements ApplicationRunner {

    private final TeamsNotificationService teamsService;

    public LocalTeamsRunner(TeamsNotificationService teamsService) {
        this.teamsService = teamsService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Build a sample notification without embedding any internal URLs or credentials.
        CampaignNotification notification = new CampaignNotification(
                "BP - Local Integration Test",
                "Open",
                "Testing a Teams notification from Spring Boot.",
                LocalDate.now().plusDays(7),
                URI.create("https://example.invalid/test")
        );

        // Trigger the existing OAuth2 and Teams gateway workflow.
        teamsService.sendCampaign(notification);
    }
}
